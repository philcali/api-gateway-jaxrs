package me.philcali.api.gateway.jaxrs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("jaxrs")
public class ResourceApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<>(Arrays.asList(ResourceModel.class));
    }

    @Override
    public Map<String, Object> getProperties() {
        ResourceModel.Configuration config = new ResourceModel.Configuration();
        config.setPort(80);
        config.setAddress("localhost");
        Map<String, Object> properties = new HashMap<>();
        properties.put("config", config);
        return properties;
    }
}
