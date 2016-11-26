package me.philcali.api.gateway.jaxrs.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;

import me.philcali.api.gateway.jaxrs.ObjectSupplier;
import me.philcali.api.gateway.jaxrs.exception.ResourceCreationException;

public class ReflectionSupplier<T> implements ObjectSupplier<T> {
    private final Map<String, Object> context;
    private final Class<T> resourceClass;
    private final Consumer<T> postConstruct;
    private static final Comparator<Constructor<?>> defaultOrder = new Comparator<Constructor<?>>() {
        @Override
        public int compare(Constructor<?> constrA, Constructor<?> constrB) {
            return Integer.compare(constrA.getParameterCount(), constrB.getParameterCount());
        }
    };

    public ReflectionSupplier(Map<String, Object> context, Class<T> resourceClass, Consumer<T> postConstruct) {
        this.context = context;
        this.resourceClass = resourceClass;
        this.postConstruct = postConstruct;
    }

    public ReflectionSupplier(Map<String, Object> context, Class<T> resourceClass) {
        this(context, resourceClass, new FillObjectConsumer<T>(context).andThen(new PostConstructConsumer<>()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        T instance = Arrays.stream(resourceClass.getConstructors()).sorted(defaultOrder).findFirst().map(construct -> {
            final Object[] params = new Object[construct.getParameterCount()];
            for (int i = 0; i < construct.getParameterCount(); i++) {
                final int index = i;
                Parameter param = construct.getParameters()[i];
                ReflectionUtils.findContextType(context, param).ifPresent(value -> params[index] = value);
            }
            try {
                return (T) construct.newInstance(params);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new ResourceCreationException(e);
            }
        }).orElseGet(() -> {
            try {
                return resourceClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ResourceCreationException(e);
            }
        });
        postConstruct.accept(instance);
        return instance;
    }
}
