package com.golovkin.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Configuration {
    private String gitBackendPath;
    private List<ProjectEntry> projectEntries;

    /**
     * Конструктор для Jackson
     */
    private Configuration() {
    }

    public Configuration(String gitBackendPath, List<ProjectEntry> projectEntries) {
        this.gitBackendPath = gitBackendPath;
        this.projectEntries = projectEntries;
    }

    public String getGitBackendPath() {
        return gitBackendPath;
    }

    public Path getGitBackendPathAsPath() {
        return Paths.get(gitBackendPath);
    }

    public List<ProjectEntry> getProjectEntries() {
        return projectEntries;
    }
}
