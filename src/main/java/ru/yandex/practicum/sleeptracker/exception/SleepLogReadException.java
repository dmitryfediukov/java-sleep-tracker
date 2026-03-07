package ru.yandex.practicum.sleeptracker.exception;

public class SleepLogReadException extends Exception {
    public SleepLogReadException(String message) {
        super(message);
    }

    public SleepLogReadException(String message, Throwable cause) {
        super(message, cause);
    }
}