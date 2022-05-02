package com.golovkin.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AppTest extends AbstractIntegrationTest {
    @Test
    public void sample() throws IOException, InterruptedException, URISyntaxException {
        gitStub().add("hi d", "hello", 1)
                .create();

        String str = new String(Files.readAllBytes(tempDir().resolve(Paths.get("git-stub/config.json"))));
        System.out.println(str);
        System.out.println(tempDir());

        String gitStubPath = Paths.get(AppTest.class.getResource("/git-stub/git-stub.jar").toURI()).toString();

        Process process = Runtime.getRuntime().exec("java -jar -Dapp.path=" + tempDir().resolve("git-stub") + " " + gitStubPath + " " + "hi d");
        process.waitFor();
        System.out.println(gitStub().readLogs());
    }
}
