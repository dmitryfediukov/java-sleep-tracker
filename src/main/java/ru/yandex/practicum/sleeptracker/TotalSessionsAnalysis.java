package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class TotalSessionsAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<?>> {
    @Override
    public SleepAnalysisResult<Integer> apply(List<SleepingSession> sessions) {
        if (sessions == null) {
            return new SleepAnalysisResult<>("Количество сессий сна", 0);
        }
        int count = sessions.size();
        return new SleepAnalysisResult<>("Количество сессий сна", count);
    }
}
