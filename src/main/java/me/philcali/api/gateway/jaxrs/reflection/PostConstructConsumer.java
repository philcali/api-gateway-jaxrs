package me.philcali.api.gateway.jaxrs.reflection;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import me.philcali.api.gateway.jaxrs.ObjectConsumer;
import me.philcali.api.gateway.jaxrs.exception.ResourceCreationException;

public class PostConstructConsumer<T> implements ObjectConsumer<T> {
    @Override
    public void accept(T instance) {
        Arrays.stream(instance.getClass().getMethods()).filter(m -> m.getAnnotation(PostConstruct.class) != null)
        .forEach(m -> {
            try {
                m.invoke(instance);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new ResourceCreationException(e);
            }
        });
    }
}
