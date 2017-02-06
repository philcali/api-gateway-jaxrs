package me.philcali.api.gateway.jaxrs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class FullHttpResponse {
    private int status;
    private Object body;
    private Map<String, String> headers;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public FullHttpResponse withStatus(int status) {
        setStatus(status);
        return this;
    }

    public FullHttpResponse withHeaders(Map<String, String> headers) {
        setHeaders(headers);
        return this;
    }

    public FullHttpResponse withBody(Object body) {
        setBody(body);
        return this;
    }

    public FullHttpResponse addHeader(String header, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(header, value);
        return this;
    }

    public FullHttpResponse addHeaderValues(String header, List<String> values) {
        return addHeader(header,
                values.stream().reduce(new StringJoiner("; "), (j, v) -> j.add(v), (j, i) -> j).toString());
    }

    public FullHttpResponse withErrorMessage(String message) {
        Map<String, String> errors = new HashMap<>();
        errors.put("errorMessage", message);
        return withBody(errors);
    }
}