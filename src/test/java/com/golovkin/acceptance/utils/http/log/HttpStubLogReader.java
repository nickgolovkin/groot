package com.golovkin.acceptance.utils.http.log;

import com.golovkin.acceptance.utils.http.Parameters;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpStubLogReader {
    private static final Path LOG_FILE_NAME = Paths.get("logs.log");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Pattern LOG_ENTRY_PATTERN = Pattern.compile("^\\[.+] (?<dateTime>.+) - Путь - \\[(?<path>.+)], метод запроса - \\[(?<method>.+)], параметры запроса - \\[(?<queryParams>.*)], тело запроса - \\[(?<requestBody>.*)], код состояния - \\[(?<statusCode>.+)], ответ - \\[(?<responseBody>.*)]$");

    private final Path path;

    public HttpStubLogReader(Path path) {
        this.path = path;
    }

    public List<HttpStubLogEntry> readLogs() {
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

    private List<HttpStubLogEntry> parseLines(List<String> lines) {
        return lines.stream()
                .filter(this::isNotEmpty)
                .map(this::parseLine)
                .collect(Collectors.toList());
    }

    private HttpStubLogEntry parseLine(String line) {
        Matcher matcher = LOG_ENTRY_PATTERN.matcher(line);
        matcher.find();

        LocalDateTime dateTime = LocalDateTime.parse(matcher.group("dateTime"), DATE_TIME_FORMATTER);
        String path = matcher.group("path");
        String method = matcher.group("method");
        Parameters queryParams = getQueryParams(matcher.group("queryParams"));

        String requestBody = matcher.group("requestBody");
        int statusCode = Integer.parseInt(matcher.group("statusCode"));
        String responseBody = matcher.group("responseBody");

        return new HttpStubLogEntry(dateTime, path, method, queryParams, requestBody, statusCode, responseBody);
    }

    private Parameters getQueryParams(String queryParamsAsString) {
        if (queryParamsAsString.isEmpty()) {
            return Parameters.create();
        }

        String[] queryParamsArr = queryParamsAsString.split(";");

        return Parameters.create(queryParamsArr);
    }

    private boolean isNotEmpty(String string) {
        return !string.isEmpty();
    }
}
