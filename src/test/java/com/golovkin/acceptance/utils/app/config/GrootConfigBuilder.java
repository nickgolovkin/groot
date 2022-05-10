package com.golovkin.acceptance.utils.app.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.golovkin.config.Configuration;
import com.golovkin.config.ProjectEntry;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GrootConfigBuilder {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Path CONFIG_FILE_NAME = Paths.get("config.json");

    private Path path;

    private String gitBackendPath;
    private String bitbucketUrl;
    private String branchNamePattern;
    private List<ProjectEntry> projectEntries;

    static {
        OBJECT_MAPPER.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public GrootConfigBuilder(Path path) {
        this.path = path;
        this.projectEntries = new ArrayList<>();
    }

    public GrootConfigBuilder withGitBackendPath(String gitBackendPath) {
        this.gitBackendPath = gitBackendPath;
        return this;
    }

    public GrootConfigBuilder withBitbucketUrl(String bitbucketUrl) {
        this.bitbucketUrl = bitbucketUrl;
        return this;
    }

    public GrootConfigBuilder withBranchNamePattern(String branchNamePattern) {
        this.branchNamePattern = branchNamePattern;
        return this;
    }

    public GrootConfigBuilder withProjectEntry(String name, String directory, String repoUrl) {
        projectEntries.add(new ProjectEntry(name, directory, repoUrl));
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
        Configuration configuration = new Configuration(gitBackendPath, bitbucketUrl, branchNamePattern, projectEntries);

        OBJECT_MAPPER.writeValue(configPath.toFile(), configuration);
    }
}
