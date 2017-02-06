package me.philcali.api.gateway.jaxrs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

import me.philcali.api.gateway.jaxrs.provider.Providers;

public class LambdaApplication extends Application {
    private final Application application;
    private final Providers providers;
    private final Map<String, Object> properties;

    public LambdaApplication(Application application, Providers providers) {
        this.application = application;
        this.providers = providers;
        this.properties = new HashMap<>(application.getProperties());
        this.properties.put(Providers.class.getName(), providers);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return application.getClasses();
    }

    @Override
    public Set<Object> getSingletons() {
        return application.getSingletons();
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    public Providers getProviders() {
        return providers;
    }
}
