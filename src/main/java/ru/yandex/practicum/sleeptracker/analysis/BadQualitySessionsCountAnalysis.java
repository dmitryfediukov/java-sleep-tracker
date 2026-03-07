package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.SleepingSession;

import java.util.List;
import java.util.function.Function;

public class BadQualitySessionsCountAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult<?>> {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Количество сессий с плохим качеством (BAD)", 0L);
        }
        long badCount = sessions.stream()
                .filter(s -> s.getQuality() == SleepQuality.BAD)
                .count();

        return new SleepAnalysisResult<>("Количество сессий с плохим качеством (BAD)", badCount);
    }
}
