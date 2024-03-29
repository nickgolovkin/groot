package com.golovkin.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Configuration {
    private String gitBackendPath;
    private String branchNamePattern;
    private List<ProjectEntry> projectEntries;

    /**
     * Конструктор для Jackson
     */
    private Configuration() {
    }

    public Configuration(String gitBackendPath, String branchNamePattern, List<ProjectEntry> projectEntries) {
        this.gitBackendPath = gitBackendPath;
        this.branchNamePattern = branchNamePattern;
        this.projectEntries = projectEntries;
    }

    public Path getGitBackendPath() {
        return Paths.get(gitBackendPath);
    }

    public String getBranchNamePattern() {
        return branchNamePattern;
    }

    public List<ProjectEntry> getProjectEntries() {
        return projectEntries;
    }
}
