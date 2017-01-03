package me.philcali.api.gateway.jaxrs.provider;

import javax.inject.Provider;
import javax.ws.rs.container.ResourceContext;

public class ResourceContextProvider implements Provider<ResourceContext> {

    @Override
    public ResourceContext get() {
        return new ResourceContext() {
            @Override
            public <T> T initResource(T resource) {
                return null;
            }

            @Override
            public <T> T getResource(Class<T> resourceClass) {
                return null;
            }
        };
    }
}
