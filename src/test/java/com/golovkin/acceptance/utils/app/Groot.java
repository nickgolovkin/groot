package com.golovkin.acceptance.utils.app;

import com.golovkin.acceptance.utils.app.config.GrootConfigBuilder;
import com.golovkin.acceptance.utils.app.exec.GrootExec;
import com.golovkin.acceptance.utils.app.log.GrootLogEntry;
import com.golovkin.acceptance.utils.app.log.GrootLogReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Groot {
    private GrootConfigBuilder grootConfigBuilder;
    private GrootLogReader grootLogReader;
    private GrootExec grootExec;
    private final Path grootDir;
    private boolean isGrootCreated;

    public Groot(Path testInstanceDir) {
        this.grootDir = testInstanceDir.resolve("groot");

        try {
            Files.createDirectory(grootDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.grootConfigBuilder = new GrootConfigBuilder(grootDir);
        this.grootLogReader = new GrootLogReader(grootDir);
        this.grootExec = new GrootExec(grootDir);
    }

    public Groot withGitBackendPath(String gitBackendPath) {
        grootConfigBuilder.withGitBackendPath(gitBackendPath);
        return this;
    }

    public Groot withProjectEntry(String name, String directory, String repoUrl) {
        grootConfigBuilder.withProjectEntry(name, directory, repoUrl);
        return this;
    }

    public void create() {
        checkGrootAlreadyCreated();
        grootConfigBuilder.create();
        isGrootCreated = true;
    }

    public void run(String arguments) {
        checkGrootNotCreated();
        grootExec.run(arguments);
    }

    public List<String> getOutput() {
        checkGrootNotCreated();
        return grootExec.getOutput();
    }

    public List<GrootLogEntry> readLogs() {
        checkGrootNotCreated();
        return grootLogReader.readLogs();
    }

    /**
     * TODO Название сбивает столку, переделать
     */
    private void checkGrootNotCreated() {
        if (!isGrootCreated) {
            throw new IllegalStateException("Groot не инициализирован! Сначала примените метод create().");
        }
    }

    private void checkGrootAlreadyCreated() {
        if (isGrootCreated) {
            throw new IllegalStateException("Groot уже был инициализирован! Создайте новый, если необходимо изменить конфигурацию.");
        }
    }
}
