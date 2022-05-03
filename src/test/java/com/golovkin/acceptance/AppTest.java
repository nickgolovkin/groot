package com.golovkin.acceptance;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class AppTest extends AbstractAcceptanceTest {
    @Test
    public void sample() throws IOException, InterruptedException, URISyntaxException {
        groot().withProjectEntry("omniutils", "somedir", "someurl")
                .create();

        groot().run("new branch myfeature");

        System.out.println(groot().getOutput());

//        gitStub().add("hi d", "hello", 1)
//                .create();
//
//        String str = new String(Files.readAllBytes(tempDir().resolve(Paths.get("git-stub/config.json"))));
//        System.out.println(str);
//        System.out.println(tempDir());
//
//        String gitStubPath = Paths.get(AppTest.class.getResource("/git-stub/git-stub.jar").toURI()).toString();
//
//        Process process = Runtime.getRuntime().exec("java -jar -Dapp.path=" + tempDir().resolve("git-stub") + " " + gitStubPath + " " + "hi d");
//        process.waitFor();
//        System.out.println(gitStub().readLogs());
    }
}
