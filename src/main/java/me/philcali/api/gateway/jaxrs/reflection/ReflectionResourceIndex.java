package me.philcali.api.gateway.jaxrs.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.philcali.api.gateway.jaxrs.FullHttpRequest;
import me.philcali.api.gateway.jaxrs.ObjectSupplier;
import me.philcali.api.gateway.jaxrs.Resource;
import me.philcali.api.gateway.jaxrs.ResourceIndex;
import me.philcali.api.gateway.jaxrs.SingletonObjectSupplier;

public class ReflectionResourceIndex implements ResourceIndex {
    private static final String STATUS_LINE = "%s %s";
    private final Application application;
    private final Map<String, Supplier<Resource>> index;
    private final ObjectMapper mapper;

    public ReflectionResourceIndex(final Application application, final ObjectMapper mapper) {
        this.application = application;
        this.mapper = mapper;
        this.index = createCachedIndex();
    }

    protected Map<String, Supplier<Resource>> createCachedIndex() {
        Map<String, Supplier<Resource>> table = new HashMap<>();
        String baseUri = getBasePath();
        for (Map.Entry<Class<?>, ObjectSupplier<?>> entry : getSuppliers().entrySet()) {
            final ObjectSupplier<?> supplier = entry.getValue();
            Path resourcePath = entry.getKey().getAnnotation(Path.class);
            if (resourcePath != null) {
                String path = baseUri + cleanedPath(resourcePath.value());
                for (Method method : entry.getKey().getMethods()) {
                    for (Annotation annotation : method.getAnnotations()) {
                        String methodName = annotation.annotationType().getSimpleName();
                        switch (methodName) {
                        case "GET":
                        case "PUT":
                        case "POST":
                        case "DELETE":
                        case "HEAD":
                        case "OPTIONS":
                            Path methodPath = method.getAnnotation(Path.class);
                            String childPath = path;
                            if (methodPath != null) {
                                childPath += cleanedPath(methodPath.value());
                            }
                            String statusLine = String.format(STATUS_LINE, methodName, childPath);
                            table.put(statusLine, () -> new ReflectionResource(application, method, mapper, supplier));
                        }
                    }
                }
            }
        }
        return table;
    }

    protected Map<Class<?>, ObjectSupplier<?>> getSuppliers() {
        Map<Class<?>, ObjectSupplier<?>> suppliers = new HashMap<>();
        for (Object singleton : application.getSingletons()) {
            suppliers.put(singleton.getClass(), new SingletonObjectSupplier<>(singleton));
        }
        for (Class<?> resourceClass : application.getClasses()) {
            suppliers.putIfAbsent(resourceClass, new ReflectionSupplier<>(application.getProperties(), resourceClass));
        }
        return suppliers;
    }

    protected String getBasePath() {
        String basePath = "/";
        ApplicationPath path = application.getClass().getAnnotation(ApplicationPath.class);
        if (path != null) {
            basePath = cleanedPath(path.value());
            if (!basePath.equals("/") && basePath.endsWith("/")) {
                basePath = basePath.substring(0, basePath.lastIndexOf('/'));
            }
        }
        return basePath;
    }

    protected String cleanedPath(final String path) {
        String base = path;
        if (!base.startsWith("/")) {
            base = "/" + path;
        }
        return base;
    }

    @Override
    public Optional<Resource> findResource(final FullHttpRequest request) {
        String statusLine = String.format(STATUS_LINE, request.getContext().getMethod(),
                request.getContext().getPath());
        return Optional.ofNullable(index.get(statusLine)).map(thunk -> thunk.get());
    }

}
