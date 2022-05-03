package com.golovkin.acceptance.utils.git;

import com.golovkin.acceptance.utils.git.config.GitStubConfigBuilder;
import com.golovkin.acceptance.utils.git.log.GitStubLogEntry;
import com.golovkin.acceptance.utils.git.log.GitStubLogReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GitStub {
    private final GitStubConfigBuilder gitStubConfigBuilder;
    private final GitStubLogReader gitStubLogReader;
    private boolean isGitStubCreated;

    public GitStub(Path testInstanceDir) {
        Path gitStubDir = testInstanceDir.resolve("git-stub");

        try {
            Files.createDirectory(gitStubDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.gitStubConfigBuilder = new GitStubConfigBuilder(gitStubDir);
        this.gitStubLogReader = new GitStubLogReader(gitStubDir);
    }

    public GitStub add(String request, String response, int exitCode) {
        checkGitStubAlreadyCreated();
        gitStubConfigBuilder.add(request, response, exitCode);
        return this;
    }

    public void create() {
        checkGitStubAlreadyCreated();
        gitStubConfigBuilder.create();
        isGitStubCreated = true;
    }

    public List<GitStubLogEntry> readLogs() {
        checkGitStubNotCreated();
        return gitStubLogReader.readLogs();
    }

    public Path getConfigPath() {
        return gitStubConfigBuilder.getConfigPath();
    }

    private void checkGitStubNotCreated() {
        if (!isGitStubCreated) {
            throw new IllegalStateException("Заглушка Git'а не инициализирована! Сначала примените метод create().");
        }
    }

    private void checkGitStubAlreadyCreated() {
        if (isGitStubCreated) {
            throw new IllegalStateException("Заглушка Git'а уже была инициализирована! Создайте новую, если необходимо изменить конфигурацию.");
        }
    }
}
