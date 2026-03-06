package ru.yandex.practicum.sleeptracker.exception;

public class InvalidSleepLogLineException extends RuntimeException {
    public InvalidSleepLogLineException(String message) {
        super(message);
    }

    public InvalidSleepLogLineException(String message, Throwable cause) {
        super(message, cause);
    }
}