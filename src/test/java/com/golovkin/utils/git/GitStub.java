package com.golovkin.utils.git;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golovkin.gitstub.config.ConfigEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GitStub {
    private static final Path CONFIG_FILE_NAME = Paths.get("config.json");
    private static final Path LOG_FILE_NAME = Paths.get("logs.log");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private Path path;
    private List<ConfigEntry> configEntries;
    public static final Pattern LOG_ENTRY_PATTERN = Pattern.compile("^\\[.+] (?<dateTime>.+) - Запрос - \\[(?<request>.+)], ответ - \\[(?<response>.+)], код ответа - \\[(?<exitCode>.+)]$");

    public GitStub(Path path) {
        this.path = path;
        this.configEntries = new ArrayList<>();
    }

    public GitStub add(String request, String response, int exitCode) {
        configEntries.add(new ConfigEntry(request, response, exitCode));
        return this;
    }

    public void create() {
        Path configPath = path.resolve(CONFIG_FILE_NAME);

        try {
            writeConfigToFile(configPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeConfigToFile(Path configPath) throws IOException {
        OBJECT_MAPPER.writeValue(configPath.toFile(), configEntries);
    }

    public List<GitStubLogEntry> readLogs() {
        Path logPath = path.resolve(LOG_FILE_NAME);

        try {
            List<String> lines = Files.readAllLines(logPath);

            return lines.stream()
                    .filter(x -> !x.isEmpty())
                    .map(this::parseLine)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
}
