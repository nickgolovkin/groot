package com.golovkin.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;

public class ConfigurationReader {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public Configuration readConfiguration() {
        try {
            Path configPath = getConfigPath();
            Validate.notNull(configPath, "Не удалось найти файл конфигурации");

            return OBJECT_MAPPER.readValue(configPath.toFile(), Configuration.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getConfigPath() {
        return ObjectUtils.firstNonNull(getSystemPropertiesConfigPath(), getAppDirectoryConfigPath());
    }

    private Path getSystemPropertiesConfigPath() {
        String appPath = System.getProperty("app.path");

        if (appPath != null) {
            Path systemPropertiesConfigPath = Paths.get(appPath).resolve("config.json");

            if (Files.exists(systemPropertiesConfigPath)) {
                return systemPropertiesConfigPath;
            }
        }

        return null;
    }

    private Path getAppDirectoryConfigPath() {
        try {
            Path appDirectoryConfigPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).toPath();

            if (Files.exists(appDirectoryConfigPath)) {
                return appDirectoryConfigPath;
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
