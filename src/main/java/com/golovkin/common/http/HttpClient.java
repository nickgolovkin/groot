package com.golovkin.common.http;

import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;

import java.io.IOException;

public class HttpClient {
    public HttpResponse execute(HttpRequest request) {
        try {
            Response response = Request.create(request.getMethod().name(), request.getHost() + request.getPath())
                    .bodyString(request.getBody(), ContentType.APPLICATION_JSON)
                    .execute();

            return new HttpResponse(response.returnResponse().getCode(), response.returnContent().asString());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
