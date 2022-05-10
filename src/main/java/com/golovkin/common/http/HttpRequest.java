package com.golovkin.common.http;

public class HttpRequest {
    private String host;
    private String path;
    private HttpMethod method;
    private String body;

    public HttpRequest(String host, String path, HttpMethod method, String body) {
        this.host = host;
        this.path = path;
        this.method = method;
        this.body = body;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getBody() {
        return body;
    }
}
