package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analysis.MaxSessionDurationAnalysis;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MaxSessionDurationAnalysisTest {

    @Test
    public void returnsZeroWhenSessionsNullOrEmpty() {
        MaxSessionDurationAnalysis analysis = new MaxSessionDurationAnalysis();

        SleepAnalysisResult<?> r1 = analysis.apply(null);
        SleepAnalysisResult<?> r2 = analysis.apply(List.of());

        assertEquals(0L, r1.getValue());
        assertEquals(0L, r2.getValue());
    }

    @Test
    public void findsMaximumDurationCorrectly() {
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