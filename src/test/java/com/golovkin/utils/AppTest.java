package com.golovkin.utils;

import com.golovkin.utils.git.GitStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppTest {
    @TempDir
    private Path tempDir;

    private GitStub gitStub;
    private String gitStubPath;

    @BeforeEach
    public void init() throws IOException, URISyntaxException {
        // Возможно, в качестве оптимизации стоит хранить jar в одном месте и просто сделать возможность
        // считывать путь до конфигурации из переменной VM
        // и запускать с переменной VM
        // и логи туда же

        Path directory = tempDir.resolve("git-stub");
        Files.createDirectory(directory);

        this.gitStub = new GitStub(directory);
        gitStubPath = Paths.get(AppTest.class.getResource("/git-stub/git-stub.jar").toURI()).toString();
    }

    @RepeatedTest(10)
    public void sample() throws IOException, InterruptedException, URISyntaxException {
        gitStub.add("hi d", "hello", 1)
                .create();

        String str = new String(Files.readAllBytes(tempDir.resolve(Paths.get("git-stub/config.json"))));
        System.out.println(str);
        System.out.println(tempDir);

        Process process = Runtime.getRuntime().exec("java -jar -Dapp.path=" + tempDir.resolve("git-stub") + " " + gitStubPath + " " + "hi d");
        process.waitFor();
        System.out.println(gitStub.readLogs());
        System.out.write(readAllBytes(process.getErrorStream()));
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 4 * 0x400; // 4KB
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                    outputStream.write(buf, 0, readLen);

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }
}
