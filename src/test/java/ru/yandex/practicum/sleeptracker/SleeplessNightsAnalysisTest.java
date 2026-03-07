package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analysis.SleeplessNightsAnalysis;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SleeplessNightsAnalysisTest {

    @Test
    public void returnsZeroWhenSessionsNullOrEmpty() {
        SleeplessNightsAnalysis analysis = new SleeplessNightsAnalysis();

        SleepAnalysisResult<?> r1 = analysis.apply(null);
        SleepAnalysisResult<?> r2 = analysis.apply(List.of());

        assertEquals(0L, r1.getValue());
        assertEquals(0L, r2.getValue());
    }

    @Test
    public void nightIsNotSleeplessWhenSessionCrossesMidnight_23to03() {
        SleeplessNightsAnalysis analysis = new SleeplessNightsAnalysis();

        // Сессия 01.10 23:00 -> 02.10 03:00 пересекает ночь 02.10 00:00-06:00
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 3, 0),
                        SleepQuality.GOOD
                )
        );

        SleepAnalysisResult<?> result = analysis.apply(sessions);

        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals(0L, result.getValue());
    }

    @Test
    public void nightIsSleeplessWhenOnlyDaySleep_07to11() {
        SleeplessNightsAnalysis analysis = new SleeplessNightsAnalysis();

        // Сон только 02.10 07:00 -> 11:00 НЕ пересекает 00:00-06:00, значит ночь 02.10 бессонная
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 7, 0),
                        LocalDateTime.of(2025, 10, 2, 11, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult<?> result = analysis.apply(sessions);

        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals(1L, result.getValue());
    }

    @Test
    public void countsSleeplessNightsAcrossSeveralDays_includingStartBeforeNoonEdgeCase() {
        SleeplessNightsAnalysis analysis = new SleeplessNightsAnalysis();

        /*
          firstStart = 01.10 08:00 (до 12:00) => startNightDate = 01.10
          lastEnd = 03.10 08:00 => endNightDate = 03.10
          Ночи: 01.10, 02.10, 03.10 (3 ночи)

          Спим только ночью 02.10 (02:00-07:00 пересекает 00-06)
          => бессонные ночи: 01.10 и 03.10 => 2
        */
        List<SleepingSession> sessions = List.of(
                // Дневная/утренняя сессия задаёт firstStart (до 12)
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 8, 0),
                        LocalDateTime.of(2025, 10, 1, 8, 30),
                        SleepQuality.NORMAL
                ),
                // Ночной сон пересекает ночь 02.10
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 2, 0),
                        LocalDateTime.of(2025, 10, 2, 7, 0),
                        SleepQuality.GOOD
                ),
                // Поздняя сессия задаёт lastEnd (до 08:00 03.10)
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 7, 30),
                        LocalDateTime.of(2025, 10, 3, 8, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult<?> result = analysis.apply(sessions);

        assertEquals("Количество бессонных ночей", result.getDescription());
        assertEquals(2L, result.getValue());
    }
}