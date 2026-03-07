package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.exception.InvalidSleepLogLineException;
import ru.yandex.practicum.sleeptracker.exception.SleepLogReadException;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Stream;

public class SleepLogReader {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    private static final String PARTS_SEPARATOR = ";";
    private static final int EXPECTED_PARTS_COUNT = 3;

    public List<SleepingSession> read(Path path) throws SleepLogReadException {
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .map(String::trim)
                    .filter(line -> !line.isBlank())
                    .map(this::parseLine)
                    .toList();
        } catch (IOException e) {
            throw new SleepLogReadException("Ошибка чтения файла лога сна: " + path, e);
        } catch (InvalidSleepLogLineException e) {
            throw new SleepLogReadException("Ошибка разбора файла лога сна: " + path, e);
        }
    }

    private SleepingSession parseLine(String line) {
        String[] parts = line.split(PARTS_SEPARATOR);
        if (parts.length != EXPECTED_PARTS_COUNT) {
            throw new InvalidSleepLogLineException("Некорректная строка лога: " + line);
        }

        try {
            LocalDateTime startDate = LocalDateTime.parse(parts[0].trim(), FORMATTER);
            LocalDateTime endDate = LocalDateTime.parse(parts[1].trim(), FORMATTER);
            SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

            return new SleepingSession(startDate, endDate, quality);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new InvalidSleepLogLineException("Некорректные данные в строке лога: " + line, e);
        }
    }
}
