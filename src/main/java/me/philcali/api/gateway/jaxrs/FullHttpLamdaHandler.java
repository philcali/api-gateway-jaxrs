package me.philcali.api.gateway.jaxrs;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Application;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.philcali.api.gateway.jaxrs.exception.ResourceException;
import me.philcali.api.gateway.jaxrs.provider.Providers;
import me.philcali.api.gateway.jaxrs.provider.ResourceContextProvider;
import me.philcali.api.gateway.jaxrs.reflection.ReflectionResourceIndex;

public abstract class FullHttpLamdaHandler implements RequestHandler<FullHttpRequest, FullHttpResponse> {
    protected static class ApplicationBuilder {
        private Set<Class<?>> resourceClasses;
        private Set<Object> singletons;
        private Map<String, Object> properties;

        public ApplicationBuilder() {
            resourceClasses = new HashSet<>();
            singletons = new HashSet<>();
            properties = new HashMap<>();
        }

        public ApplicationBuilder addResource(Class<?> resource) {
            resourceClasses.add(resource);
            return this;
        }

        public ApplicationBuilder addResource(Object resource) {
            singletons.add(resource);
            return this;
        }

        public ApplicationBuilder registerConfig(Object config) {
            Arrays.stream(config.getClass().getDeclaredFields()).forEach(field -> {
                try {
                    field.setAccessible(true);
                    properties.put(field.getName(), field.get(config));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new ResourceException(e);
                }
            });
            return this;
        }

        public ApplicationBuilder registerConfig(String property, Object value) {
            properties.put(property, value);
            return this;
        }

        public Application build() {
            return new Application() {
                @Override
                public Set<Class<?>> getClasses() {
                    return Collections.unmodifiableSet(resourceClasses);
                }

                @Override
                public Set<Object> getSingletons() {
                    return Collections.unmodifiableSet(singletons);
                }

                @Override
                public Map<String, Object> getProperties() {
                    return Collections.unmodifiableMap(properties);
                }
            };
        }
    }

    protected abstract Application getApplication();

    public abstract String getVersion();

    @Override
    public FullHttpResponse handleRequest(FullHttpRequest request, Context context) {
        Application application = new LambdaApplication(getApplication(), new Providers()
                .addProvider(request)
                .addProvider(context)
                .addProvider(ResourceContext.class, new ResourceContextProvider()));
        Middleware middleware = new Middleware(new ReflectionResourceIndex(application, new ObjectMapper()));
        return middleware.apply(request);
    }
}