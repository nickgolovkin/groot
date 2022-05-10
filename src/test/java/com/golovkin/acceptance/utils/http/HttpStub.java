package com.golovkin.acceptance.utils.http;

import com.golovkin.acceptance.utils.http.config.HttpStubConfigBuilder;
import com.golovkin.acceptance.utils.http.log.HttpStubLogEntry;
import com.golovkin.acceptance.utils.http.log.HttpStubLogReader;
import com.golovkin.acceptance.utils.http.log.HttpRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpStub {
    private final HttpStubConfigBuilder httpStubConfigBuilder;
    private final HttpStubLogReader httpStubLogReader;
    private boolean isHttpStubCreated;
    private final Path httpStubDir;
    private Process process;

    public HttpStub(Path testInstanceDir) {
        httpStubDir = testInstanceDir.resolve("http-stub");

        try {
            Files.createDirectory(httpStubDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.httpStubConfigBuilder = new HttpStubConfigBuilder(httpStubDir);
        this.httpStubLogReader = new HttpStubLogReader(httpStubDir);
    }

    public HttpStub add(String requestPath, String requestMethod, String requestBody, int statusCode, String responseBody) {
        checkHttpStubAlreadyCreated();
        httpStubConfigBuilder.add(requestPath, requestMethod, requestBody, statusCode, responseBody);
        return this;
    }

    public HttpStub add(String requestPath, String requestMethod, Parameters parameters, String requestBody, int statusCode, String responseBody) {
        checkHttpStubAlreadyCreated();
        httpStubConfigBuilder.add(requestPath, requestMethod, parameters, requestBody, statusCode, responseBody);
        return this;
    }

    public void create() {
        checkHttpStubAlreadyCreated();
        httpStubConfigBuilder.create();
        try {
            String httpStubPath = Paths.get(getClass().getResource("/http-stub/http-stub.jar").toURI()).toString();

            String processString = String.format("java -jar -Dapp.path=%s %s", httpStubDir.toString(), httpStubPath);
            process = Runtime.getRuntime().exec(processString);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        isHttpStubCreated = true;
    }

    public void stop() {
        process.destroy();
        isHttpStubCreated = false;
    }

    public List<HttpStubLogEntry> readLogs() {
        checkHttpStubNotCreated();
        return httpStubLogReader.readLogs();
    }

    public List<HttpRequest> readRequestsFromLog() {
        return readLogs().stream()
                .map(HttpStubLogEntry::getHttpRequest)
                .collect(Collectors.toList());
    }

    public Path getHttpStubDirectoryPath() {
        return httpStubDir;
    }

    public Path getConfigPath() {
        return httpStubConfigBuilder.getConfigPath();
    }

    private void checkHttpStubNotCreated() {
        if (!isHttpStubCreated) {
            throw new IllegalStateException("Заглушка http не инициализирована! Сначала примените метод create().");
        }
    }

    private void checkHttpStubAlreadyCreated() {
        if (isHttpStubCreated) {
            throw new IllegalStateException("Заглушка http уже была инициализирована! Создайте новую, если необходимо изменить конфигурацию.");
        }
    }

    public boolean isHttpStubCreated() {
        return isHttpStubCreated;
    }
}
