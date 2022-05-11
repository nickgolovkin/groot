package com.golovkin.git.exceptions;

public class UnknownCommand extends RuntimeException {
    private String command;

    public UnknownCommand(String command) {
        super(String.format("Не удалось обработать команду [%s]", command));
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}