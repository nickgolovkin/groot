package com.golovkin;

import com.golovkin.config.Configuration;
import com.golovkin.config.ConfigurationReader;
import com.golovkin.dialogs.AbstractDialog;
import com.golovkin.dialogs.utils.DialogSearcher;
import com.golovkin.dialogs.DialogInputParser;
import com.golovkin.dialogs.newbranch.NewBranchDialog;
import com.golovkin.git.Git;

import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main( String[] args )
    {
        String input = String.join(" ", args);

        Configuration configuration = new ConfigurationReader().readConfiguration();
        Git git = new Git(configuration.getGitBackendPath());

        Map<Class<? extends AbstractDialog>, AbstractDialog> dialogs = getDialogs(git, configuration);
        DialogSearcher dialogSearcher = new DialogSearcher(dialogs);

        AbstractDialog dialog = dialogSearcher.searchDialog(input);
        DialogInputParser inputParser = dialog.getInputParser();
        dialog.start(inputParser.parse(input));
    }

    @SuppressWarnings("rawtypes")
    private static Map<Class<? extends AbstractDialog>, AbstractDialog> getDialogs(Git git, Configuration configuration) {
        Map<Class<? extends AbstractDialog>, AbstractDialog> abstractDialogs = new HashMap<>();

        abstractDialogs.put(NewBranchDialog.class, new NewBranchDialog(git, configuration.getProjectEntries()));

        return abstractDialogs;
    }
}
