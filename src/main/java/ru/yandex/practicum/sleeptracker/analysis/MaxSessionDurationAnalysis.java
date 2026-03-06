package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.util.List;
import java.util.function.Function;

public class MaxSessionDurationAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<?>> {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Максимальная длительность сессии (мин)", 0L);
        }
        long max = sessions.stream()
                .mapToLong(SleepingSession::durationMinutes)
                .max()
                .orElse(0L);

        return new SleepAnalysisResult<>("Максимальная длительность сессии (мин)", max);
    }
}
