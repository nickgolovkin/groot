package com.golovkin.acceptance.utils.http;

import java.util.HashMap;
import java.util.Map;

public class Parameters {
    private Map<String, String> map;

    private Parameters() {}

    public static Parameters create(String... params) {
        Parameters parameters = new Parameters();
        parameters.map = new HashMap<>();

        for (String param : params) {
            String[] keyValuePairs = param.split("=");
            if (keyValuePairs.length % 2 != 0) {
                throw new IllegalArgumentException("Неверная пара ключ/значение");
            }

            parameters.map.put(keyValuePairs[0], keyValuePairs[1]);
        }

        return parameters;
    }

    public Map<String, String> asMap() {
        return map;
    }
}
