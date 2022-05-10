package com.golovkin.acceptance.utils.http.log;

import com.golovkin.acceptance.utils.http.Parameters;

import java.time.LocalDateTime;
import java.util.Map;

public class HttpStubLogEntry {
    private final LocalDateTime dateTime;
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;


    public HttpStubLogEntry(LocalDateTime dateTime, String requestPath, String requestMethod, Parameters requestQueryParams, String requestBody, int responseStatus, String responseBody) {
        this.dateTime = dateTime;
        this.httpRequest = new HttpRequest(requestPath, requestMethod, requestQueryParams, requestBody);
        this.httpResponse = new HttpResponse(responseStatus, responseBody);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }
}
