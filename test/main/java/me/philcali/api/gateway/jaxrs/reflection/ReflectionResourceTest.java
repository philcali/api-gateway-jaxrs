package me.philcali.api.gateway.jaxrs.reflection;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.philcali.api.gateway.jaxrs.Resource;
import me.philcali.api.gateway.jaxrs.ResourceMethod;
import me.philcali.api.gateway.jaxrs.model.ResourceApplication;
import me.philcali.api.gateway.jaxrs.model.ResourceModel;
import me.philcali.api.gateway.jaxrs.model.ResourceModel.Configuration;

public class ReflectionResourceTest {
    private Resource resource;
    private ObjectMapper mapper;
    private Application application;
    private Configuration config;
    private ResourceModel model;

    @Before
    public void setUp() throws Exception {
        config = new Configuration();
        config.setAddress("localhost");
        config.setPort(8080);
        mapper = new ObjectMapper();
        application = new ResourceApplication();
        model = new ResourceModel(config);
        resource = new ReflectionResource(application, mapper, ResourceModel.class, () -> model, "/jaxrs");
    }

    @Test
    public void testGetMethods() throws NoSuchMethodException, SecurityException {
        Method getTest = ResourceModel.class.getMethod("getTest");
        Method per = ResourceModel.class.getMethod("getPerson");
        Method echo = ResourceModel.class.getMethod("echo", String.class);
        Set<ResourceMethod> methods = new HashSet<ResourceMethod>(
                Arrays.asList(
                        new ReflectionResourceMethod(application, resource, getTest, mapper, "GET", Optional.empty()),
                        new ReflectionResourceMethod(application, resource, per, mapper, "GET", Optional.of("person")),
                        new ReflectionResourceMethod(application, resource, echo, mapper, "GET", Optional.of("echo"))));
        assertEquals(methods, resource.getMethods());
    }

    @Test
    public void testGetPath() {
        assertEquals("/jaxrs", resource.getPath());
    }

    @Test
    public void testGetSupplier() {
        assertEquals(model, resource.getSupplier().get());
    }
}
