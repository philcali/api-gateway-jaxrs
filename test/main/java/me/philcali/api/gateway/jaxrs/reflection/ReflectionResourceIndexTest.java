package me.philcali.api.gateway.jaxrs.reflection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.philcali.api.gateway.jaxrs.Context;
import me.philcali.api.gateway.jaxrs.FullHttpRequest;
import me.philcali.api.gateway.jaxrs.Resource;
import me.philcali.api.gateway.jaxrs.ResourceIndex;
import me.philcali.api.gateway.jaxrs.model.ResourceApplication;

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
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindResourceSubPath() {
        FullHttpRequest request = generateRequest("/jaxrs/tests/echo", "GET");
        Optional<Resource> result = index.findResource(request);
        assertTrue(result.isPresent());
    }

    @Test
    public void testSingletonPath() {
        FullHttpRequest request = generateRequest("/jaxrs/singleton", "GET");
        Optional<Resource> result = index.findResource(request);
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindNonExistentResource() {
        FullHttpRequest request = generateRequest("/not/found", "GET");
        Optional<Resource> result = index.findResource(request);
        assertFalse(result.isPresent());
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
