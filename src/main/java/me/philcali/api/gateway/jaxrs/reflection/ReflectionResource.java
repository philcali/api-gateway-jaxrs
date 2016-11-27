package me.philcali.api.gateway.jaxrs.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.philcali.api.gateway.jaxrs.Resource;
import me.philcali.api.gateway.jaxrs.ResourceMethod;

public class ReflectionResource implements Resource {
    private final Application application;
    private final ObjectMapper mapper;
    private final Class<?> resourceClass;
    private final Supplier<?> supplier;
    private final String basePath;
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
        for (Method method : resourceClass.getMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                String methodName = annotation.annotationType().getSimpleName();
                switch (methodName) {
                case "GET":
                case "PUT":
                case "POST":
                case "DELETE":
                case "HEAD":
                case "OPTIONS":
                    Optional<String> path = Optional.ofNullable(method.getAnnotation(Path.class)).map(p -> p.value());
                    methods.add(new ReflectionResourceMethod(application, this, method, mapper, methodName, path));
                }
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
}
