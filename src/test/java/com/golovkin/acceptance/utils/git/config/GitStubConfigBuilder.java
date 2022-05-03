package com.golovkin.acceptance.utils.git.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golovkin.gitstub.config.ConfigEntry;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GitStubConfigBuilder {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Path CONFIG_FILE_NAME = Paths.get("config.json");

    private List<ConfigEntry> configEntries;
    private Path path;

    public GitStubConfigBuilder(Path path) {
        this.configEntries = new ArrayList<>();
        this.path = path;
    }

    public GitStubConfigBuilder add(String request, String response, int exitCode) {
        configEntries.add(new ConfigEntry(request, response, exitCode));
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
