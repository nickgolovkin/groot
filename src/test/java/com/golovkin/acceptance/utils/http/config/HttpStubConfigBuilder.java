package com.golovkin.acceptance.utils.http.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golovkin.acceptance.utils.http.Parameters;
import com.golovkin.httpstub.config.ConfigEntry;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpStubConfigBuilder {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Path CONFIG_FILE_NAME = Paths.get("config.json");

    private List<ConfigEntry> configEntries;
    private Path path;

    public HttpStubConfigBuilder(Path path) {
        this.configEntries = new ArrayList<>();
        this.path = path;
    }

    public HttpStubConfigBuilder add(String requestPath, String requestMethod, String requestBody, int statusCode, String responseBody) {
        return add(requestPath, requestMethod, Parameters.create(), requestBody, statusCode, responseBody);
    }

    public HttpStubConfigBuilder add(String requestPath, String requestMethod, Parameters parameters, String requestBody, int statusCode, String responseBody) {
        configEntries.add(new ConfigEntry(requestMethod, requestPath, parameters.asMap(), requestBody, statusCode, responseBody));
        return this;
    }

    public void create() {
        Path configPath = getConfigPath();

        try {
            writeConfigToFile(configPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Path getConfigPath() {
        return path.resolve(CONFIG_FILE_NAME);
    }

    private void writeConfigToFile(Path configPath) throws IOException {
        OBJECT_MAPPER.writeValue(configPath.toFile(), configEntries);
    }
}
