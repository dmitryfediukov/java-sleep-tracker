package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TotalSessionsAnalysisTest {

    @Test
    void returnsZeroWhenSessionsIsNull() {
        TotalSessionsAnalysis analysis = new TotalSessionsAnalysis();

        SleepAnalysisResult<?> result = analysis.apply(null);

        assertEquals("Количество сессий сна", result.getDescription());
        assertEquals(0, result.getValue());
    }

    @Test
    void countsSessionsCorrectly() {
        TotalSessionsAnalysis analysis = new TotalSessionsAnalysis();

        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 7, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult<?> result = analysis.apply(sessions);

        assertEquals("Количество сессий сна", result.getDescription());
        assertEquals(2, result.getValue());
    }
}