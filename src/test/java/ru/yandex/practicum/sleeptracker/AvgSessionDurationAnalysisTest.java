package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analysis.AvgSessionDurationAnalysis;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AvgSessionDurationAnalysisTest {

    @Test
    public void returnsZeroWhenSessionsNullOrEmpty() {
        AvgSessionDurationAnalysis analysis = new AvgSessionDurationAnalysis();

        SleepAnalysisResult<?> r1 = analysis.apply(null);
        SleepAnalysisResult<?> r2 = analysis.apply(List.of());

        assertEquals(0.0, (Double) r1.getValue(), 1e-9);
        assertEquals(0.0, (Double) r2.getValue(), 1e-9);
    }

    @Test
    public void calculatesAverageCorrectly() {
        AvgSessionDurationAnalysis analysis = new AvgSessionDurationAnalysis();

        // 60 и 120 минут -> среднее 90.0
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 14, 0),
                        LocalDateTime.of(2025, 10, 1, 15, 0),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 14, 0),
                        LocalDateTime.of(2025, 10, 2, 16, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult<?> result = analysis.apply(sessions);

        assertEquals("Средняя длительность сессии (мин)", result.getDescription());
        assertEquals(90.0, (Double) result.getValue(), 1e-9);
    }
}