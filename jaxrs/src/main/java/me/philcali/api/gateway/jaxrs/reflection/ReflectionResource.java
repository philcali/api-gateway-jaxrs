package me.philcali.api.gateway.jaxrs.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.philcali.api.gateway.jaxrs.PathUtils;
import me.philcali.api.gateway.jaxrs.Resource;
import me.philcali.api.gateway.jaxrs.ResourceMethod;
import me.philcali.api.gateway.jaxrs.exception.ResourceCreationException;

public class ReflectionResource implements Resource {
    private final Application application;
    private final ObjectMapper mapper;
    private final Class<?> resourceClass;
    private final Supplier<?> supplier;
    private final String basePath;
    private Set<Resource> resources;
    private Set<ResourceMethod> methods;

    public ReflectionResource(Application application, ObjectMapper mapper, Class<?> resourceClass,
            Supplier<?> supplier, String basePath) {
        this.application = application;
        this.mapper = mapper;
        this.supplier = supplier;
        this.resourceClass = resourceClass;
        this.basePath = basePath;
        init();
    }

    protected void init() {
        methods = new HashSet<>();
        resources = new HashSet<>();
        for (Method method : resourceClass.getMethods()) {
            boolean isMethod = false;
            Optional<String> path = Optional.ofNullable(method.getAnnotation(Path.class)).map(p -> p.value());
            for (Annotation annotation : method.getAnnotations()) {
                String methodName = annotation.annotationType().getSimpleName();
                switch (methodName) {
                case "GET":
                case "PUT":
                case "POST":
                case "DELETE":
                case "HEAD":
                case "OPTIONS":
                    methods.add(new ReflectionResourceMethod(application, this, method, mapper, methodName, path));
                    isMethod = true;
                }
            }
            if (!isMethod && path.isPresent()) {
                // TODO: cleanup supplier with a supplier factory given context
                Class<?> returnType = method.getReturnType();
                Supplier<?> childSupplier = null;
                if (returnType.isAssignableFrom(Class.class)) {
                    ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
                    returnType = (Class<?>) type.getActualTypeArguments()[0];
                    childSupplier = new ReflectionSupplier<>(application.getProperties(), returnType);
                } else {
                    childSupplier = () -> {
                        Object thing = supplier.get();
                        try {
                            return method.invoke(thing);
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            throw new ResourceCreationException(e);
                        }
                    };
                }
                resources.add(new ReflectionResource(application, mapper, returnType, childSupplier,
                        basePath + path.map(PathUtils::normalize).get()));
            }
        }
    }

    @Override
    public Set<ResourceMethod> getMethods() {
        return Collections.unmodifiableSet(methods);
    }

    @Override
    public Supplier<?> getSupplier() {
        return supplier;
    }

    @Override
    public String getPath() {
        return basePath;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMethods(), basePath);
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj) || !(obj instanceof Resource)) {
            return false;
        }
        Resource resource = (Resource) obj;
        return Objects.deepEquals(getMethods(), resource.getMethods()) && Objects.equals(resource.getPath(), getPath());
    }

    @Override
    public Set<Resource> getResources() {
        return Collections.unmodifiableSet(resources);
    }
}
