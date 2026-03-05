package ru.yandex.practicum.sleeptracker;

import java.util.Objects;

public final class SleepAnalysisResult<T> {
    private final String description;
    private final T value;

    public SleepAnalysisResult(String description, T value) {
        this.description = Objects.requireNonNull(description);
        this.value = Objects.requireNonNull(value);
    }

    public String getDescription() {
        return description;
    }

    public T getValue() {
        return value;
    }
}
