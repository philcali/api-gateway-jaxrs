package me.philcali.api.gateway.jaxrs.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import me.philcali.api.gateway.jaxrs.model.ResourceModel.Configuration;

@ApplicationPath("jaxrs")
public class ResourceApplication extends Application {
    private final Configuration config;

    public ResourceApplication() {
        config = new Configuration();
        config.setAddress("localhost");
        config.setPort(8080);
    }

    public ResourceApplication(Configuration config) {
        this.config = config;
    }

    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<>(Arrays.asList(ResourceModel.class));
    }

    @Override
    public Set<Object> getSingletons() {
        return new HashSet<>(Arrays.asList(new SingletonResourceModel(config)));
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("config", config);
        properties.put("person", new TestObject("Philip", 99));
        return properties;
    }
}
