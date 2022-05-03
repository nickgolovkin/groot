package com.golovkin.acceptance.utils.app.exec;

import com.golovkin.acceptance.utils.PathUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GrootExec {
    private final Path path;
    private Process process;

    public GrootExec(Path path) {
        this.path = path;
    }

    public void run(String arguments) {
        try {
            Path grootJarPath = PathUtils.getResourcePath("/groot/groot.jar");
            process = Runtime.getRuntime().exec("java -jar -Dapp.path=" + path.toString() + " " + grootJarPath.toString() + " " + arguments);
            process.waitFor();
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

    private List<String> getOutputFromStream(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        List<String> output = new ArrayList<>();

        while (scanner.hasNextLine()) {
            output.add(scanner.nextLine());
        }

        return output;
    }
}
