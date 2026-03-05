package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MaxSessionDurationAnalysisTest {

    @Test
    void returnsZeroWhenSessionsNullOrEmpty() {
        MaxSessionDurationAnalysis analysis = new MaxSessionDurationAnalysis();

        SleepAnalysisResult<?> r1 = analysis.apply(null);
        SleepAnalysisResult<?> r2 = analysis.apply(List.of());

        assertEquals(0L, r1.getValue());
        assertEquals(0L, r2.getValue());
    }

    @Test
    void findsMaximumDurationCorrectly() {
        MaxSessionDurationAnalysis analysis = new MaxSessionDurationAnalysis();

        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 0),
                        LocalDateTime.of(2025, 10, 2, 7, 0),
                        SleepQuality.GOOD
                ), // 480
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 0),
                        LocalDateTime.of(2025, 10, 3, 15, 30),
                        SleepQuality.NORMAL
                )  // 90
        );

        SleepAnalysisResult<?> result = analysis.apply(sessions);

        assertEquals("Максимальная длительность сессии (мин)", result.getDescription());
        assertEquals(480L, result.getValue());
    }
}