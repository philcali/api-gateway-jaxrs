package me.philcali.api.gateway.jaxrs;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class FullHttpResponseTest {

    @Test
    public void testAddHeaderValues() {
        FullHttpResponse response = new FullHttpResponse();
        response.addHeaderValues("Content-Type", Arrays.asList("text/plain", "charset=utf8"));
        assertEquals("text/plain; charset=utf8", response.getHeaders().get("Content-Type"));
    }

    @Test
    public void testErrorMessage() {
        FullHttpResponse response = new FullHttpResponse();
        response.withErrorMessage("Test message");
        Map<String, Object> message = new HashMap<>();
        message.put("errorMessage", "Test message");
        assertEquals(message, response.getBody());
    }
}
