package me.philcali.api.gateway.jaxrs;

import java.util.Optional;

import javax.ws.rs.core.Application;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ReflectionResourceIndexTest {
    private ResourceIndex index;

    @Before
    public void setUp() {
        Application application = new ResourceApplication();
        ObjectMapper mapper = new ObjectMapper();
        index = new ReflectionResourceIndex(application, mapper);
    }

    @Test
    public void testFindResource() {
        FullHttpRequest request = generateRequest("/jaxrs/tests", "GET");
        Optional<Resource> result = index.findResource(request);
        Assert.assertTrue(result.isPresent());
    }

    @Test
    public void testFindNonExistentResource() {
        FullHttpRequest request = generateRequest("/not/found", "GET");
        Optional<Resource> result = index.findResource(request);
        Assert.assertFalse(result.isPresent());
    }

    private FullHttpRequest generateRequest(String path, String method) {
        FullHttpRequest request = new FullHttpRequest();
        Context context = new Context();
        context.setPath(path);
        context.setMethod(method);
        request.setContext(context);
        return request;
    }
}
