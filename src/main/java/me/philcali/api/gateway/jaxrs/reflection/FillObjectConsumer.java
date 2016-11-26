package me.philcali.api.gateway.jaxrs.reflection;

import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.core.Context;

import me.philcali.api.gateway.jaxrs.ObjectConsumer;
import me.philcali.api.gateway.jaxrs.exception.ResourceCreationException;

public class FillObjectConsumer<T> implements ObjectConsumer<T> {
    private final Map<String, Object> context;

    public FillObjectConsumer(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public void accept(T instance) {
        Arrays.stream(instance.getClass().getDeclaredFields()).filter(f -> f.getAnnotation(Context.class) != null)
        .forEach(f -> {
            ReflectionUtils.findContextType(context, f.getType()).ifPresent(value -> {
                try {
                    f.setAccessible(true);
                    f.set(instance, value);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new ResourceCreationException(e);
                }
            });
        });
    }
}
