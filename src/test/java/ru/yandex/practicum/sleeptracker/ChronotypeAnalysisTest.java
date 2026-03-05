package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChronotypeAnalysisTest {

    @Test
    void returnsDoveWhenSessionsNullOrEmpty() {
        ChronotypeAnalysis analysis = new ChronotypeAnalysis();

        SleepAnalysisResult<?> r1 = analysis.apply(null);
        SleepAnalysisResult<?> r2 = analysis.apply(List.of());

        assertEquals("Хронотип пользователя", r1.getDescription());
        assertEquals(Chronotype.DOVE, r1.getValue());
        assertEquals(Chronotype.DOVE, r2.getValue());
    }

    @Test
    void returnsOwlWhenOwlNightsAreMajority() {
        ChronotypeAnalysis analysis = new ChronotypeAnalysis();

        /*
          1) 01.10 23:30 -> 02.10 10:00 => ночь 02.10: OWL (start>23, wake>9)
          2) 02.10 23:40 -> 03.10 09:30 => ночь 03.10: OWL
          3) 03.10 21:30 -> 04.10 06:30 => ночь 04.10: LARK (start<22, wake<7)
          Итого: OWL=2, LARK=1 => OWL
        */
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 10, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 40),
                        LocalDateTime.of(2025, 10, 3, 9, 30),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 21, 30),
                        LocalDateTime.of(2025, 10, 4, 6, 30),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult<?> result = analysis.apply(sessions);

        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals(Chronotype.OWL, result.getValue());
    }

    @Test
    void returnsDoveOnTieBetweenOwlAndLark() {
        ChronotypeAnalysis analysis = new ChronotypeAnalysis();

        /*
          OWL=1, LARK=1 => сомнения => DOVE по ТЗ
        */
        List<SleepingSession> sessions = List.of(
                // OWL ночь 02.10
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 10, 0),
                        SleepQuality.GOOD
                ),
                // LARK ночь 03.10
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 21, 30),
                        LocalDateTime.of(2025, 10, 3, 6, 30),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult<?> result = analysis.apply(sessions);

        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals(Chronotype.DOVE, result.getValue());
    }

    @Test
    void ignoresSleeplessNightsAndDayNaps() {
        ChronotypeAnalysis analysis = new ChronotypeAnalysis();

        /*
          firstStart = 01.10 14:00 (после 12) => startNightDate = 02.10
          lastEnd = 03.10 15:00 => endNightDate = 03.10
          Ночи: 02.10 и 03.10

          Есть сон в ночь 02.10 => OWL (23:30 -> 10:00)
          Ночь 03.10 бессонная => должна игнорироваться в хронотипе
          Дневной сон 01.10 14:00-15:00 не пересекает 00-06 и не должен влиять
          => итог OWL
        */
        List<SleepingSession> sessions = List.of(
                // дневная сессия (nap)
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 14, 0),
                        LocalDateTime.of(2025, 10, 1, 15, 0),
                        SleepQuality.NORMAL
                ),
                // ночной OWL сон (пересекает 02.10 00-06)
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 10, 0),
                        SleepQuality.GOOD
                ),
                // ещё один дневной сон, чтобы lastEnd оказался 03.10
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 30),
                        LocalDateTime.of(2025, 10, 3, 15, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult<?> result = analysis.apply(sessions);

        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals(Chronotype.OWL, result.getValue());
    }
}