package com.golovkin.acceptance.utils;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {
    public static Path getResourcePath(String resourcePath) {
        try {
            return Paths.get(PathUtils.class.getResource(resourcePath).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
