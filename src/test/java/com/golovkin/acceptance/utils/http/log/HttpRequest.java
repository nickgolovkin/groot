package com.golovkin.acceptance.utils.http.log;

import com.golovkin.acceptance.utils.http.Parameters;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String requestMethod;
    private final String requestPath;
    private final Parameters requestQueryParams;
    private final String requestBody;

    public HttpRequest(String requestPath, String requestMethod, String requestBody) {
        this(requestPath, requestMethod, Parameters.create(), requestBody);
    }

    public HttpRequest(String requestPath, String requestMethod, Parameters requestQueryParams, String requestBody) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.requestQueryParams = requestQueryParams;
        this.requestBody = requestBody;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Parameters getRequestQueryParams() {
        return requestQueryParams;
    }

    public String getRequestBody() {
        return requestBody;
    }

    @Override
    public String toString() {
        return String.format("Путь - [%s], метод - [%s], параметры - [%s], тело - [%s]", requestPath, requestMethod, requestQueryParams, requestBody);
    }
}
