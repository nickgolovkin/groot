package com.golovkin.acceptance.utils.http.log;

public class HttpResponse {
    private final int responseStatus;
    private final String responseBody;

    public HttpResponse(int responseStatus, String responseBody) {
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
