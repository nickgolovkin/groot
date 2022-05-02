package com.golovkin.utils;

import com.golovkin.utils.git.GitStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractIntegrationTest {
    @TempDir
    private Path tempDir;

    private GitStub gitStub;

    @BeforeEach
    public void init() throws IOException {
        // Возможно, в качестве оптимизации стоит хранить jar в одном месте и просто сделать возможность
        // считывать путь до конфигурации из переменной VM
        // и запускать с переменной VM
        // и логи туда же

        // Наличие временной директории - это деталь реализации, ты не должен о ней задумываться, поэтому нужно как-то сделать ее общей
        // для всех расширений (либо просто сделай абстрактный класс, куда вынесешь это всё и всё)

        Path directory = tempDir.resolve("git-stub");
        Files.createDirectory(directory);

        this.gitStub = new GitStub(directory);
    }

    protected Path tempDir() {
        return tempDir;
    }

    protected GitStub gitStub() {
        return gitStub;
    }
}
