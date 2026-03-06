package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analysis.MinSessionDurationAnalysis;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MinSessionDurationAnalysisTest {

    @Test
    public void returnsZeroWhenSessionsNullOrEmpty() {
        MinSessionDurationAnalysis analysis = new MinSessionDurationAnalysis();

        SleepAnalysisResult<?> r1 = analysis.apply(null);
        SleepAnalysisResult<?> r2 = analysis.apply(List.of());

        assertEquals(0L, r1.getValue());
        assertEquals(0L, r2.getValue());
    }

    @Test
    public void findsMinimumDurationCorrectly() {
        MinSessionDurationAnalysis analysis = new MinSessionDurationAnalysis();

        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 0),
                        SleepQuality.GOOD
                ), // 480
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 0),
                        LocalDateTime.of(2025, 10, 3, 14, 40),
                        SleepQuality.NORMAL
                )  // 40
        );

        SleepAnalysisResult<?> result = analysis.apply(sessions);

        assertEquals("Минимальная длительность сессии (мин)", result.getDescription());
        assertEquals(40L, result.getValue());
    }
}