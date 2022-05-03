package com.golovkin.git;

import com.golovkin.RegexUtils;

import java.util.regex.Pattern;

public class GitUtils {
    public static final String BRANCH_NAME_FROM_STATUS_PATTERN = "On branch (?<branchName>[^\\s]+)";
    private Git git;

    public GitUtils(Git git) {
        this.git = git;
    }

    public String getCurrentBranch(String projectDirectoryPath) {
        try {
            String status = git.status(projectDirectoryPath);

            return RegexUtils.extractSubstring(status, BRANCH_NAME_FROM_STATUS_PATTERN, "branchName");
        } catch (Exception e) {
            return null;
        }
    }
}
