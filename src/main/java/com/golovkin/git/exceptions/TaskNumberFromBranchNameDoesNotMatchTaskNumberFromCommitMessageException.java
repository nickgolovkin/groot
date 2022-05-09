package com.golovkin.git.exceptions;

public class TaskNumberFromBranchNameDoesNotMatchTaskNumberFromCommitMessageException extends GitException {
    private String taskNumberFromBranchName;
    private String taskNumberFromCommitMessage;

    public TaskNumberFromBranchNameDoesNotMatchTaskNumberFromCommitMessageException(String taskNumberFromBranchName, String taskNumberFromCommitMessage) {
        super("Номер таски из названия ветки не совпадает с номером таски из сообщения коммита");
        this.taskNumberFromBranchName = taskNumberFromBranchName;
        this.taskNumberFromCommitMessage = taskNumberFromCommitMessage;
    }

    public String getTaskNumberFromBranchName() {
        return taskNumberFromBranchName;
    }

    public String getTaskNumberFromCommitMessage() {
        return taskNumberFromCommitMessage;
    }
}
