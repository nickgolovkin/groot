package com.golovkin.acceptance.utils.app.log;

import com.golovkin.acceptance.utils.common.log.LogLevel;
import com.golovkin.acceptance.utils.git.log.GitStubLogEntry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GrootLogReader {
    private static final Path LOG_FILE_NAME = Paths.get("logs.log");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Pattern LOG_ENTRY_PATTERN = Pattern.compile("^\\[(?<logLevel>.+?)] (?<dateTime>.+?) - (?<message>.+)$");

    private final Path path;

    public GrootLogReader(Path path) {
        this.path = path;
    }

    public List<GrootLogEntry> readLogs() {
        Path logPath = path.resolve(LOG_FILE_NAME);

        try {
            List<String> lines = Files.readAllLines(logPath);

            return parseLines(lines);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<GrootLogEntry> parseLines(List<String> lines) {
        return lines.stream()
                .filter(this::isNotEmpty)
                .map(this::parseLine)
                .collect(Collectors.toList());
    }

    private GrootLogEntry parseLine(String line) {
        Matcher matcher = LOG_ENTRY_PATTERN.matcher(line);
        matcher.find();

        LocalDateTime dateTime = LocalDateTime.parse(matcher.group("dateTime"), DATE_TIME_FORMATTER);
        LogLevel logLevel = LogLevel.valueOf(matcher.group("logLevel").trim());
        String message = matcher.group("message");

        return new GrootLogEntry(dateTime, logLevel, message);
    }

    private boolean isNotEmpty(String string) {
        return !string.isEmpty();
    }
}
