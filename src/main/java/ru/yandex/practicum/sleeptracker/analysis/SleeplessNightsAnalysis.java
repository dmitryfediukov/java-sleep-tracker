package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class SleeplessNightsAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<?>> {

    private static final LocalTime NIGHT_START = LocalTime.of(0, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);

    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Количество бессонных ночей", 0L);
        }

        Optional<LocalDateTime> firstStartOpt = sessions.stream()
                .map(SleepingSession::getStartDate)
                .min(Comparator.naturalOrder());

        Optional<LocalDateTime> lastEndOpt = sessions.stream()
                .map(SleepingSession::getEndDate)
                .max(Comparator.naturalOrder());

        if (firstStartOpt.isEmpty() || lastEndOpt.isEmpty()) {
            return new SleepAnalysisResult<>("Количество бессонных ночей", 0L);
        }

        LocalDateTime firstStart = firstStartOpt.get();
        LocalDateTime lastEnd = lastEndOpt.get();

        //Определяем дату первой ночи
        LocalDate startNightDate;
        if (firstStart.toLocalTime().isAfter(LocalTime.NOON)) {
            startNightDate = firstStart.toLocalDate().plusDays(1);
        } else {
            startNightDate = firstStart.toLocalDate();
        }

        //Определяем дату последней ночи
        LocalDate endNightDate = lastEnd.toLocalDate();

        //Считаем количество ночей
        long nightsCount = countDatesInclusive(startNightDate, endNightDate);
        if (nightsCount <= 0) {
            return new SleepAnalysisResult<>("Количество бессонных ночей", 0L);
        }

        //Считаем количество бессонных ночей
        long sleepless = Stream.iterate(startNightDate, d -> d.plusDays(1))
                .limit(nightsCount)
                .filter(nightDate -> isSleeplessNight(sessions, nightDate))
                .count();

        return new SleepAnalysisResult<>("Количество бессонных ночей", sleepless);
    }

    //Проверяем, была ли ночь бессонной
    private boolean isSleeplessNight(List<SleepingSession> sessions, LocalDate nightDate) {
        LocalDateTime nightStart = LocalDateTime.of(nightDate, NIGHT_START);
        LocalDateTime nightEnd = LocalDateTime.of(nightDate, NIGHT_END);

        boolean sleptThatNight = sessions.stream()
                .anyMatch(s -> intersects(s.getStartDate(), s.getEndDate(), nightStart, nightEnd));

        return !sleptThatNight;
    }

    // Пересечение полуинтервалов: [start, end) и [intervalStart, intervalEnd)
    private boolean intersects(LocalDateTime sessionStart, LocalDateTime sessionEnd,
                               LocalDateTime intervalStart, LocalDateTime intervalEnd) {
        return sessionStart.isBefore(intervalEnd) && sessionEnd.isAfter(intervalStart);
    }

    private long countDatesInclusive(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) return 0;
        return ChronoUnit.DAYS.between(start, end) + 1;
    }
}
