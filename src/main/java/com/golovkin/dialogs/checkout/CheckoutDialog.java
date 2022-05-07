package com.golovkin.dialogs.checkout;

import com.golovkin.config.ProjectEntry;
import com.golovkin.dialogs.AbstractDialog;
import com.golovkin.git.Git;
import com.golovkin.git.GitUtils;
import com.golovkin.git.commands.checkout.CheckoutGitCommand;
import com.golovkin.git.commands.checkout.CheckoutGitCommandInput;
import com.golovkin.git.commands.checkout.CheckoutGitCommandOutput;
import com.golovkin.git.commands.deletebranch.DeleteBranchGitCommandInput;
import com.golovkin.git.exceptions.BranchNotFoundException;
import com.golovkin.git.exceptions.RefNotExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CheckoutDialog extends AbstractDialog<CheckoutDialogInput, CheckoutDialogInputParser> {
    private final static Logger LOGGER = LoggerFactory.getLogger(CheckoutDialog.class);

    public CheckoutDialog(Git git, List<ProjectEntry> projectEntries) {
        super(git, projectEntries);
    }

    @Override
    public void start(CheckoutDialogInput input) {
        CheckoutGitCommand checkoutGitCommand = new CheckoutGitCommand(getGit());

        GitUtils gitUtils = new GitUtils(getGit());

        String branchName = input.getBranchName();

        System.out.printf("Перехожу в ветку [%s]\n", branchName);

        for (ProjectEntry projectEntry : getProjectEntries()) {
            String projectName = projectEntry.getName();
            String currentBranchName = gitUtils.getCurrentBranch(projectEntry.getDirectory());

            try {
                CheckoutGitCommandInput commandInput = new CheckoutGitCommandInput(branchName, projectEntry.getDirectory());
                CheckoutGitCommandOutput commandOutput = checkoutGitCommand.execute(commandInput);

                System.out.printf("[%s] Переход в ветку [%s] успешно завершен\n", projectName, branchName);

                if (commandOutput.isNothingToCommit()) {
                    LOGGER.info("[{}] Переход из ветки [{}] в ветку [{}]. Переход в ветку [{}] успешно завершен (несохраненных изменений не было). Команды - [{}]",
                            projectName, currentBranchName, branchName, branchName, getGit().getLastExecutedCommandsAsString());
                } else {
                    LOGGER.info("[{}] Переход из ветки [{}] в ветку [{}]. Переход в ветку [{}] успешно завершен. Команды - [{}]",
                            projectName, currentBranchName, branchName, branchName, getGit().getLastExecutedCommandsAsString());
                }
            } catch (RefNotExistsException e) {
                System.out.printf("[%s] Ветка [%s] не найдена\n", projectName, branchName);
                LOGGER.error("[{}] Переход из ветки [{}] в ветку [{}]. Ветка [{}] не найдена. Команды - [{}]",
                        projectName, currentBranchName, branchName, branchName, getGit().getLastExecutedCommandsAsString());
            } catch (Exception e) {
                System.out.printf("[%s] Не удалось перейти в ветку [%s]\n", projectName, branchName);
                LOGGER.error("[{}] Переход из ветки [{}] в ветку [{}]. Не удалось перейти в ветку [{}]. Причина ошибки - [{}]. Команды - [{}]",
                        projectName, currentBranchName, branchName, branchName, e.getMessage(), getGit().getLastExecutedCommandsAsString());
            }
        }

        System.out.printf("Переход в ветку [%s] завершен\n", branchName);
    }

    @Override
    public CheckoutDialogInputParser getInputParser() {
        return new CheckoutDialogInputParser();
    }
}
