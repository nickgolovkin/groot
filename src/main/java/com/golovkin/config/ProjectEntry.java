package com.golovkin.config;

public class ProjectEntry {
    private String name;
    private String directory;
    private String repoUrl;

    /**
     * Конструктор для Jackson
     */
    private ProjectEntry() {
    }

    public ProjectEntry(String name, String directory, String repoUrl) {
        this.name = name;
        this.directory = directory;
        this.repoUrl = repoUrl;
    }

    public String getName() {
        return name;
    }

    public String getDirectory() {
        return directory;
    }

    public String getRepoUrl() {
        return repoUrl;
    }
}
