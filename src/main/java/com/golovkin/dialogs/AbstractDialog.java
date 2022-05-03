package com.golovkin.dialogs;

import com.golovkin.config.ProjectEntry;
import com.golovkin.git.Git;

import java.util.List;

public abstract class AbstractDialog<Input extends DialogInput, InputParser extends DialogInputParser<Input>> {
    private final List<ProjectEntry> projectEntries;
    private final Git git;

    public AbstractDialog(Git git, List<ProjectEntry> projectEntries) {
        this.projectEntries = projectEntries;
        this.git = git;
    }

    public abstract void start(Input input);

    public abstract InputParser getInputParser();

    protected List<ProjectEntry> getProjectEntries() {
        return projectEntries;
    }

    protected Git getGit() {
        return git;
    }
}
