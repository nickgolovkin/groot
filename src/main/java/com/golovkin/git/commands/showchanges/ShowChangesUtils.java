package com.golovkin.git.commands.showchanges;

import com.golovkin.RegexUtils;
import com.golovkin.git.exceptions.GitException;

import java.util.List;
import java.util.regex.Pattern;

public class ShowChangesUtils {
    public static final Pattern CHECKPOINT_COMMIT_PATTERN = Pattern.compile("(?<hash>[A-z|0-9]+).+commit: \\[GROOT\\] ~Show changes checkpoint~");
    public static final int EXPECTED_CHECKPOINT_COMMIT_INDEX_IN_REFLOG = 1;

    public static String getCheckpointHash(List<String> reflog) {
        if (reflog.size() < EXPECTED_CHECKPOINT_COMMIT_INDEX_IN_REFLOG + 1) {
            return null;
        }

        return RegexUtils.extractSubstring(reflog.get(EXPECTED_CHECKPOINT_COMMIT_INDEX_IN_REFLOG), CHECKPOINT_COMMIT_PATTERN, "hash");
    }
}
