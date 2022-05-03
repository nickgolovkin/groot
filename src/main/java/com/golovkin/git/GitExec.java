package com.golovkin.git;

import com.golovkin.common.exceptions.TimeoutException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GitExec {
    private final Path path;
    private Process process;
    private int exitCode;
    private List<String> lastExecutedCommands;

    public GitExec(Path path) {
        this.path = path;
        this.lastExecutedCommands = new ArrayList<>();
    }

    public void run(String command) {
        try {
            lastExecutedCommands.add(command);
            String consoleCommand = String.format("%s %s", path.toString(), command);
            process = Runtime.getRuntime().exec(consoleCommand);

            if (!process.waitFor(1, TimeUnit.SECONDS)) {
                throw new TimeoutException(String.format("Время ожидания выполнения команды [%s] вышло", command));
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getOutput() {
        List<String> output = new ArrayList<>();
        output.addAll(getOutputFromStream(process.getInputStream()));
        output.addAll(getOutputFromStream(process.getErrorStream()));

        return output;
    }

    public int getExitCode() {
        return process.exitValue();
    }

    private List<String> getOutputFromStream(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        List<String> output = new ArrayList<>();

        while (scanner.hasNextLine()) {
            output.add(scanner.nextLine());
        }

        return output;
    }

    public List<String> getLastExecutedCommands() {
        return lastExecutedCommands;
    }

    public void resetLastExecutedCommands() {
        this.lastExecutedCommands = new ArrayList<>();
    }
}
