package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.Chronotype;
import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class ChronotypeAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<?>> {

    private static final LocalTime NIGHT_START = LocalTime.of(0, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);

    private static final LocalTime OWL_SLEEP_AFTER = LocalTime.of(23, 0);
    private static final LocalTime OWL_WAKE_AFTER = LocalTime.of(9, 0);

    private static final LocalTime LARK_SLEEP_BEFORE = LocalTime.of(22, 0);
    private static final LocalTime LARK_WAKE_BEFORE = LocalTime.of(7, 0);

    @Override
    public SleepAnalysisResult<Chronotype> apply(List<SleepingSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Хронотип пользователя", Chronotype.DOVE);
        }

        Optional<LocalDateTime> firstStartOpt = sessions.stream()
                .map(SleepingSession::getStartDate)
                .min(Comparator.naturalOrder());

        Optional<LocalDateTime> lastEndOpt = sessions.stream()
                .map(SleepingSession::getEndDate)
                .max(Comparator.naturalOrder());

        if (firstStartOpt.isEmpty() || lastEndOpt.isEmpty()) {
            return new SleepAnalysisResult<>("Хронотип пользователя", Chronotype.DOVE);
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
            return new SleepAnalysisResult<>("Хронотип пользователя", Chronotype.DOVE);
        }

        Map<Chronotype, Long> counts = Stream.iterate(startNightDate, d -> d.plusDays(1))
                .limit(nightsCount)
                .map(nightDate -> classifyNightIfNotSleepless(sessions, nightDate))
                .flatMap(Optional::stream)
                .collect(java.util.stream.Collectors.groupingBy(
                        ct -> ct,
                        java.util.stream.Collectors.counting()
                ));

        long owls = counts.getOrDefault(Chronotype.OWL, 0L);
        long larks = counts.getOrDefault(Chronotype.LARK, 0L);
        long doves = counts.getOrDefault(Chronotype.DOVE, 0L);

        Chronotype winner = chooseWinner(owls, larks, doves);

        return new SleepAnalysisResult<>("Хронотип пользователя", winner);
    }

    private Optional<Chronotype> classifyNightIfNotSleepless(List<SleepingSession> sessions, LocalDate nightDate) {
        LocalDateTime nightStart = LocalDateTime.of(nightDate, NIGHT_START);
        LocalDateTime nightEnd = LocalDateTime.of(nightDate, NIGHT_END);

        List<SleepingSession> nightSessions = sessions.stream()
                .filter(s -> intersects(s.getStartDate(), s.getEndDate(), nightStart, nightEnd))
                .toList();

        if (nightSessions.isEmpty()) {
            // бессонная ночь — игнорируем
            return Optional.empty();
        }

        LocalDateTime fallAsleep = nightSessions.stream()
                .map(SleepingSession::getStartDate)
                .min(Comparator.naturalOrder())
                .orElseThrow();

        LocalDateTime wakeUp = nightSessions.stream()
                .map(SleepingSession::getEndDate)
                .max(Comparator.naturalOrder())
                .orElseThrow();

        Chronotype nightType = classifyByTimes(fallAsleep.toLocalTime(), wakeUp.toLocalTime());
        return Optional.of(nightType);
    }

    private Chronotype classifyByTimes(LocalTime sleepStart, LocalTime wakeTime) {
        boolean owl = sleepStart.isAfter(OWL_SLEEP_AFTER) && wakeTime.isAfter(OWL_WAKE_AFTER);
        if (owl) return Chronotype.OWL;

        boolean lark = sleepStart.isBefore(LARK_SLEEP_BEFORE) && wakeTime.isBefore(LARK_WAKE_BEFORE);
        if (lark) return Chronotype.LARK;

        return Chronotype.DOVE;
    }

    private Chronotype chooseWinner(long owls, long larks, long doves) {
        long max = Math.max(doves, Math.max(owls, larks));

        boolean owlsTop = owls == max;
        boolean larksTop = larks == max;
        boolean dovesTop = doves == max;

        int tops = (owlsTop ? 1 : 0) + (larksTop ? 1 : 0) + (dovesTop ? 1 : 0);

        // Если есть равенство за 1 место (tops > 1) — считаем голубём
        if (tops > 1) return Chronotype.DOVE;

        if (owlsTop) return Chronotype.OWL;
        if (larksTop) return Chronotype.LARK;
        return Chronotype.DOVE;
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
