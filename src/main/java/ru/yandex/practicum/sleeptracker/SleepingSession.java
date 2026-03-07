package ru.yandex.practicum.sleeptracker;


import ru.yandex.practicum.sleeptracker.exception.InvalidSleepingSessionException;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public final class SleepingSession {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final SleepQuality quality;

    public SleepingSession(LocalDateTime startDate, LocalDateTime endDate, SleepQuality quality) {
        this.startDate = Objects.requireNonNull(startDate, "startDate");
        this.endDate = Objects.requireNonNull(endDate, "endDate");
        this.quality = Objects.requireNonNull(quality, "quality");

        if (!endDate.isAfter(startDate)) {
            throw new InvalidSleepingSessionException(
                    "Время пробуждения должно быть позже времени засыпания: " + startDate + " -> " + endDate);
        }
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public long durationMinutes() {
        return Duration.between(startDate, endDate).toMinutes();
    }

    @Override
    public String toString() {
        return "SleepingSession{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", quality=" + quality +
                '}';
    }
}
