package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BadQualitySessionsCountAnalysisTest {

    @Test
    void returnsZeroWhenSessionsNullOrEmpty() {
        BadQualitySessionsCountAnalysis analysis = new BadQualitySessionsCountAnalysis();

        SleepAnalysisResult<?> r1 = analysis.apply(null);
        SleepAnalysisResult<?> r2 = analysis.apply(List.of());

        assertEquals(0L, r1.getValue());
        assertEquals(0L, r2.getValue());
    }

    @Test
    void countsBadSessionsCorrectly() {
        BadQualitySessionsCountAnalysis analysis = new BadQualitySessionsCountAnalysis();

        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.BAD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 0),
                        LocalDateTime.of(2025, 10, 2, 15, 0),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 7, 0),
                        SleepQuality.BAD
                )
        );

        SleepAnalysisResult<?> result = analysis.apply(sessions);

        assertEquals("Количество сессий с плохим качеством (BAD)", result.getDescription());
        assertEquals(2L, result.getValue());
    }
}