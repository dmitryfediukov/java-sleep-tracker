package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class MinSessionDurationAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<?>> {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Минимальная длительность сессии (мин)", 0L);
        }
        long min = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .min()
                .orElse(0L);

        return new SleepAnalysisResult<>("Минимальная длительность сессии (мин)", min);
    }
}
