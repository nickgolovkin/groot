package com.golovkin;

import com.golovkin.config.Configuration;
import com.golovkin.config.ConfigurationReader;
import com.golovkin.dialogs.AbstractDialog;
import com.golovkin.dialogs.abort.AbortDialog;
import com.golovkin.dialogs.checkout.CheckoutDialog;
import com.golovkin.dialogs.deletebranch.DeleteBranchDialog;
import com.golovkin.dialogs.renamebranch.RenameBranchDialog;
import com.golovkin.dialogs.resettocommit.ResetToCommitDialog;
import com.golovkin.dialogs.showchanges.ShowChangesDialog;
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
        abstractDialogs.put(DeleteBranchDialog.class, new DeleteBranchDialog(git, configuration.getProjectEntries()));
        abstractDialogs.put(RenameBranchDialog.class, new RenameBranchDialog(git, configuration.getProjectEntries()));
        abstractDialogs.put(AbortDialog.class, new AbortDialog(git, configuration.getProjectEntries()));
        abstractDialogs.put(CheckoutDialog.class, new CheckoutDialog(git, configuration.getProjectEntries()));
        abstractDialogs.put(ResetToCommitDialog.class, new ResetToCommitDialog(git, configuration.getProjectEntries()));
        abstractDialogs.put(ShowChangesDialog.class, new ShowChangesDialog(git, configuration.getProjectEntries()));

        return abstractDialogs;
    }
}
