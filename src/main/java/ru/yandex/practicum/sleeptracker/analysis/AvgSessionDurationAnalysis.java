package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.util.List;
import java.util.function.Function;

public class AvgSessionDurationAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<?>> {
    @Override
    public SleepAnalysisResult<Double> apply(List<SleepingSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Средняя длительность сессии (мин)", 0.0);
        }
        double avg = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .average()
                .orElse(0.0);

        return new SleepAnalysisResult<>("Средняя длительность сессии (мин)", avg);
    }
}
