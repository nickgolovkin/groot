package com.golovkin.acceptance.utils.git.log;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GitStubLogReader {
    private static final Path LOG_FILE_NAME = Paths.get("logs.log");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Pattern LOG_ENTRY_PATTERN = Pattern.compile("^\\[.+] (?<dateTime>.+) - Запрос - \\[(?<request>.+)], ответ - \\[(?<response>.*)], код ответа - \\[(?<exitCode>.+)]$");

    private final Path path;

    public GitStubLogReader(Path path) {
        this.path = path;
    }

    public List<GitStubLogEntry> readLogs() {
        Path logPath = path.resolve(LOG_FILE_NAME);

        try {
            List<String> lines = Files.readAllLines(logPath);

            return parseLines(lines);
        } catch (NoSuchFileException e) {
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<GitStubLogEntry> parseLines(List<String> lines) {
        return lines.stream()
                .filter(this::isNotEmpty)
                .map(this::parseLine)
                .collect(Collectors.toList());
    }

    private GitStubLogEntry parseLine(String line) {
        Matcher matcher = LOG_ENTRY_PATTERN.matcher(line);
        matcher.find();

        LocalDateTime dateTime = LocalDateTime.parse(matcher.group("dateTime"), DATE_TIME_FORMATTER);
        String request = matcher.group("request");
        String response = matcher.group("response");
        int exitCode = Integer.parseInt(matcher.group("exitCode"));

        return new GitStubLogEntry(dateTime, request, response, exitCode);
    }

    private boolean isNotEmpty(String string) {
        return !string.isEmpty();
    }
}
