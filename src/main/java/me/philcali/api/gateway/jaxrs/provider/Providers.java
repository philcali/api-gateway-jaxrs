package me.philcali.api.gateway.jaxrs.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Provider;

public class Providers {
    private final Map<Class<?>, Provider<?>> providers;

    public Providers() {
        this(new HashMap<>());
    }

    public Providers(Map<Class<?>, Provider<?>> providers) {
        this.providers = providers;
    }

    public <T> Providers addProvider(Class<T> targetClass, Provider<T> provider) {
        providers.put(targetClass, provider);
        return this;
    }

    public Providers addProvider(Object thing) {
        providers.put(thing.getClass(), () -> thing);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> provide(Class<T> targetClass) {
        return Optional.ofNullable(providers.get(targetClass)).map(provider -> (T) provider.get());
    }
}
