package me.philcali.api.gateway.jaxrs.reflection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.philcali.api.gateway.jaxrs.FullHttpRequest;
import me.philcali.api.gateway.jaxrs.FullHttpResponse;
import me.philcali.api.gateway.jaxrs.Resource;
import me.philcali.api.gateway.jaxrs.ResourceMethod;
import me.philcali.api.gateway.jaxrs.model.ResourceApplication;
import me.philcali.api.gateway.jaxrs.model.ResourceModel;
import me.philcali.api.gateway.jaxrs.model.ResourceModel.Configuration;

public class ReflectionResourceMethodTest {
    private ResourceMethod method;
    private Method jMethod;
    private Configuration config;
    private ResourceApplication application;
    private ObjectMapper mapper;
    private Resource resource;

    @Before
    public void setUp() throws NoSuchMethodException, SecurityException {
        config = new Configuration();
        config.setAddress("localhost");
        config.setPort(8080);
        application = new ResourceApplication(config);
        mapper = new ObjectMapper();
        resource = mock(Resource.class);
        Supplier<ResourceModel> supplier = () -> new ResourceModel(config);
        when(resource.getSupplier()).thenAnswer(invoke -> supplier);
        jMethod = ResourceModel.class.getMethod("getTest");
        method = new ReflectionResourceMethod(application, resource, jMethod, mapper, "GET", Optional.empty());
    }

    @Test
    public void testApply() {
        FullHttpRequest request = buildRequest();
        FullHttpResponse fullResponse = method.apply(request);
        assertEquals(200, fullResponse.getStatus());
        assertEquals(config, fullResponse.getBody());
        assertEquals("application/json", fullResponse.getHeaders().get("Content-Type"));
    }

    private FullHttpRequest buildRequest() {
        FullHttpRequest request = new FullHttpRequest();
        Map<String, String> params = new HashMap<>();
        params.put("Accept", "application/json");
        request.setParams(params);
        return request;
    }
}
