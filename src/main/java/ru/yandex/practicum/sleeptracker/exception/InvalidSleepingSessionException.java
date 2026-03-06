package ru.yandex.practicum.sleeptracker.exception;

public class InvalidSleepingSessionException extends RuntimeException {
    public InvalidSleepingSessionException(String message) {
        super(message);
    }
}