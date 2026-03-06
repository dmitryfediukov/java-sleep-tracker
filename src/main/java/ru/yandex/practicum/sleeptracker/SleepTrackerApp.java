package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analysis.*;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class SleepTrackerApp {

    private static final SleepLogReader reader = new SleepLogReader();

    private static final List<Function<List<SleepingSession>, SleepAnalysisResult<?>>> analyses = List.of(
            new TotalSessionsAnalysis(),
            new MinSessionDurationAnalysis(),
            new MaxSessionDurationAnalysis(),
            new AvgSessionDurationAnalysis(),
            new BadQualitySessionsCountAnalysis(),
            new SleeplessNightsAnalysis(),
            new ChronotypeAnalysis()
    );

    public static void main(String[] args) {
        try {
            List<SleepingSession> sessions;
            try {
                sessions = reader.read(Path.of("src/main/resources/sleep_log.txt"));
            } catch (Exception e) {
                System.out.println("Ошибка при чтении файла: " + e.getMessage());
                return;
            }

            analyses.stream()
                    .map(f -> f.apply(sessions))
                    .forEach(SleepTrackerApp::printResult);

        } catch (Exception e) {
            System.out.println("Неопределенная ошибка: " + e);
            e.printStackTrace();
        }
    }

    private static void printResult(SleepAnalysisResult<?> result) {
        System.out.println(result.getDescription() + ": " + result.getValue());
    }
}