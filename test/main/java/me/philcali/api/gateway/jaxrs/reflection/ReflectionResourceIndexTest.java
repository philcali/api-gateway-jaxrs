package me.philcali.api.gateway.jaxrs.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.philcali.api.gateway.jaxrs.Context;
import me.philcali.api.gateway.jaxrs.FullHttpRequest;
import me.philcali.api.gateway.jaxrs.ResourceIndex;
import me.philcali.api.gateway.jaxrs.ResourceMethod;
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
    public void testFindMethod() {
        FullHttpRequest request = generateRequest("/jaxrs/tests", "GET");
        Optional<ResourceMethod> result = index.findMethod(request);
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindResourceSubPath() {
        FullHttpRequest request = generateRequest("/jaxrs/tests/echo", "GET");
        Optional<ResourceMethod> result = index.findMethod(request);
        assertTrue(result.isPresent());
    }

    @Test
    public void testSingletonPath() {
        FullHttpRequest request = generateRequest("/jaxrs/singleton", "GET");
        Optional<ResourceMethod> result = index.findMethod(request);
        assertTrue(result.isPresent());
    }

    @Test
    public void testFindNonExistentResource() {
        FullHttpRequest request = generateRequest("/not/found", "GET");
        Optional<ResourceMethod> result = index.findMethod(request);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetResources() {
        Set<String> statusLines = index.getResources().stream().flatMap(resource -> resource.getMethods().stream())
                .map(method -> method.getMethod() + " " + method.getPath()).collect(Collectors.toSet());
        Set<String> expectedLines = new HashSet<>(
                Arrays.asList("GET /jaxrs/tests", "GET /jaxrs/tests/person", "GET /jaxrs/tests/echo",
                        "GET /jaxrs/singleton", "POST /jaxrs/singleton"));
        assertEquals(expectedLines, statusLines);
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
