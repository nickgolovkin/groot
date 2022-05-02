package com.golovkin.utils.git;

import com.golovkin.utils.git.config.GitStubConfigBuilder;
import com.golovkin.utils.git.log.GitStubLogEntry;
import com.golovkin.utils.git.log.GitStubLogReader;

import java.nio.file.Path;
import java.util.List;

public class GitStub {
    private final GitStubConfigBuilder gitStubConfigBuilder;
    private final GitStubLogReader gitStubLogReader;
    private boolean isGitStubCreated;

    public GitStub(Path path) {
        this.gitStubConfigBuilder = new GitStubConfigBuilder(path);
        this.gitStubLogReader = new GitStubLogReader(path);
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
