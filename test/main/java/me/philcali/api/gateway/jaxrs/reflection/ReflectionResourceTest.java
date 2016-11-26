package me.philcali.api.gateway.jaxrs.reflection;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.philcali.api.gateway.jaxrs.FullHttpRequest;
import me.philcali.api.gateway.jaxrs.FullHttpResponse;
import me.philcali.api.gateway.jaxrs.Resource;
import me.philcali.api.gateway.jaxrs.model.ResourceApplication;
import me.philcali.api.gateway.jaxrs.model.ResourceModel;
import me.philcali.api.gateway.jaxrs.model.ResourceModel.Configuration;
import me.philcali.api.gateway.jaxrs.reflection.ReflectionResource;

public class ReflectionResourceTest {
    private Resource resource;
    private ObjectMapper mapper;
    private Application application;
    private Configuration config;

    @Before
    public void setUp() throws Exception {
        config = new Configuration();
        config.setAddress("localhost");
        config.setPort(8080);
        mapper = new ObjectMapper();
        application = new ResourceApplication();
        Method method = ResourceModel.class.getMethod("getTest");
        resource = new ReflectionResource(application, method, mapper, () -> new ResourceModel(config));
    }

    @Test
    public void testApply() {
        FullHttpRequest request = buildRequest();
        FullHttpResponse fullResponse = resource.apply(request);
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
