package com.golovkin.acceptance;

import com.golovkin.acceptance.utils.PathUtils;
import com.golovkin.acceptance.utils.app.Groot;
import com.golovkin.acceptance.utils.common.GrootChecker;
import com.golovkin.acceptance.utils.git.GitStub;
import com.golovkin.acceptance.utils.http.HttpStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public abstract class AbstractAcceptanceTest {
    @TempDir
    private Path tempDir;

    private GitStub gitStub;
    private HttpStub httpStub;
    private Groot groot;
    private GrootChecker grootChecker;

    @BeforeEach
    public void init() throws IOException {
        // Возможно, в качестве оптимизации стоит хранить jar в одном месте и просто сделать возможность
        // считывать путь до конфигурации из переменной VM
        // и запускать с переменной VM
        // и логи туда же

        // Наличие временной директории - это деталь реализации, ты не должен о ней задумываться, поэтому нужно как-то сделать ее общей
        // для всех расширений (либо просто сделай абстрактный класс, куда вынесешь это всё и всё)
        this.gitStub = new GitStub(tempDir);
        this.httpStub = new HttpStub(tempDir);
        this.groot = new Groot(tempDir);
        this.groot.withGitBackendPath(String.format("java -jar -Dapp.path=%s %s", gitStub.getGitStubDirectoryPath().toString(), PathUtils.getResourcePath("/git-stub/git-stub.jar").toString()));
        this.groot.withBitbucketUrl("localhost:1080");
        this.grootChecker = new GrootChecker(groot, gitStub, httpStub);
    }

    @AfterEach
    public void tearDown() {
        if (httpStub.isHttpStubCreated()) {
            httpStub.stop();
        }
    }

    protected Path tempDir() {
        return tempDir;
    }

    protected GitStub gitStub() {
        return gitStub;
    }

    protected HttpStub httpStub() {
        return httpStub;
    }

    protected Groot groot() {
        return groot;
    }

    protected GrootChecker check() {
        return grootChecker;
    }
}
