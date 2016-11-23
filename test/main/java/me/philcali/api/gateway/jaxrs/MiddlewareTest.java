package me.philcali.api.gateway.jaxrs;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class MiddlewareTest {
    private Middleware middleware;
    private ResourceIndex index;

    @Before
    public void setUp() {
        index = Mockito.mock(ResourceIndex.class);
        middleware = new Middleware(index);
    }

    @Test
    public void testApplyEmpty() {
        FullHttpRequest request = new FullHttpRequest();
        Mockito.when(index.findResource(request)).thenAnswer(invoke -> Optional.empty());
        FullHttpResponse response = middleware.apply(request);
        Map<String, Object> message = new HashMap<>();
        message.put("errorMessage", "Resource not found.");
        Assert.assertEquals(404, response.getStatus());
        Assert.assertEquals(message, response.getBody());
    }

    @Test
    public void testApply() {
        FullHttpRequest request = new FullHttpRequest();
        FullHttpResponse response = new FullHttpResponse();
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(index.findResource(request)).thenAnswer(invoke -> {
            return Optional.of(resource);
        });
        Mockito.when(resource.apply(request)).thenAnswer(invoke -> {
            return response.withStatus(204).addHeader("X-Test", "Something");
        });
        FullHttpResponse expectedResponse = middleware.apply(request);
        Map<String, String> expectedHeaders = new HashMap<>();
        expectedHeaders.put("X-Test", "Something");
        Assert.assertEquals(expectedResponse, response);
        Assert.assertEquals(204, response.getStatus());
        Assert.assertEquals(expectedHeaders, response.getHeaders());
    }
}
