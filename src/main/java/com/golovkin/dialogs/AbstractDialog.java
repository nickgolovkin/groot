package com.golovkin.dialogs;

import com.golovkin.config.ProjectEntry;

import java.util.List;

public abstract class AbstractDialog<Input extends com.golovkin.dialogs.Input, InputParser extends com.golovkin.dialogs.InputParser<Input>> {
    private final List<ProjectEntry> projectEntries;

    public AbstractDialog(List<ProjectEntry> projectEntries) {
        this.projectEntries = projectEntries;
    }

    public abstract void start(Input input);

    public abstract InputParser getInputParser();

    protected List<ProjectEntry> getProjectEntries() {
        return projectEntries;
    }
}
