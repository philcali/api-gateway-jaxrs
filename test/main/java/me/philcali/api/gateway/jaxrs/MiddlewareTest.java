package me.philcali.api.gateway.jaxrs;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import me.philcali.api.gateway.jaxrs.exception.ResourceCreationException;

public class MiddlewareTest {
    private Middleware middleware;
    private ResourceIndex index;

    @Before
    public void setUp() {
        index = mock(ResourceIndex.class);
        middleware = new Middleware(index);
    }

    @Test
    public void testApplyEmpty() {
        FullHttpRequest request = new FullHttpRequest();
        when(index.findMethod(request)).thenAnswer(invoke -> Optional.empty());
        FullHttpResponse response = middleware.apply(request);
        Map<String, Object> message = new HashMap<>();
        message.put("errorMessage", "Resource not found.");
        assertEquals(404, response.getStatus());
        assertEquals(message, response.getBody());
    }

    @Test
    public void testDefaultError() {
        FullHttpRequest request = new FullHttpRequest();
        ResourceMethod method = mock(ResourceMethod.class);
        when(index.findMethod(request)).thenAnswer(invoke -> Optional.of(method));
        when(method.apply(request)).thenThrow(new ResourceCreationException(new RuntimeException("Test exception")));
        FullHttpResponse response = middleware.apply(request);
        Map<String, Object> message = new HashMap<>();
        message.put("errorMessage", "Test exception");
        assertEquals(500, response.getStatus());
        assertEquals(message, response.getBody());
    }

    @Test
    public void testApply() {
        FullHttpRequest request = new FullHttpRequest();
        FullHttpResponse response = new FullHttpResponse();
        ResourceMethod method = mock(ResourceMethod.class);
        when(index.findMethod(request)).thenAnswer(invoke -> Optional.of(method));
        when(method.apply(request)).thenAnswer(invoke -> {
            return response.withStatus(204).addHeader("X-Test", "Something");
        });
        FullHttpResponse expectedResponse = middleware.apply(request);
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("X-Test", "Something");
        assertEquals(expectedResponse, response);
        assertEquals(204, response.getStatus());
        assertEquals(expectedHeaders, response.getHeaders());
    }
}
