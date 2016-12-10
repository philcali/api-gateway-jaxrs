package me.philcali.api.gateway.jaxrs.reflection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.philcali.api.gateway.jaxrs.Context;
import me.philcali.api.gateway.jaxrs.FullHttpRequest;
import me.philcali.api.gateway.jaxrs.Resource;
import me.philcali.api.gateway.jaxrs.ResourceIndex;
import me.philcali.api.gateway.jaxrs.ResourceMethod;
import me.philcali.api.gateway.jaxrs.SingletonObjectSupplier;

public class ReflectionResourceIndex implements ResourceIndex {
    private static final String STATUS_LINE = "%s %s";
    private final Application application;
    private final ObjectMapper mapper;
    private final String basePath;
    private Map<String, ResourceMethod> index;

    public ReflectionResourceIndex(final Application application, final ObjectMapper mapper) {
        this.application = application;
        this.mapper = mapper;
        this.basePath = getBasePath();
        init();
    }

    protected void init() {
        index = new HashMap<>();
        for (Map.Entry<Class<?>, Supplier<?>> entry : getSuppliers().entrySet()) {
            final Supplier<?> supplier = entry.getValue();
            Path resourcePath = entry.getKey().getAnnotation(Path.class);
            if (resourcePath != null) {
                String path = basePath + cleanedPath(resourcePath.value());
                Resource resource = new ReflectionResource(application, mapper, entry.getKey(), supplier, path);
                resource.getAllMethods().stream().forEach(method -> {
                    index.put(String.format(STATUS_LINE, method.getMethod(), method.getPath()), method);
                });
            }
        }
    }

    protected Map<Class<?>, Supplier<?>> getSuppliers() {
        Map<Class<?>, Supplier<?>> suppliers = new HashMap<>();
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
    public Optional<ResourceMethod> findMethod(final FullHttpRequest request) {
        Context context = request.getContext();
        String statusLine = String.format(STATUS_LINE, context.getMethod(), context.getPath());
        return Optional.ofNullable(index.get(statusLine));
    }

    @Override
    public Set<Resource> getResources() {
        return index.values().stream().map(method -> method.getResource()).collect(Collectors.toSet());
    }

    @Override
    public String getPath() {
        return basePath;
    }
}
