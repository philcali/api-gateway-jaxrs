package me.philcali.api.gateway.jaxrs;

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

public class ReflectionResourceIndex implements ResourceIndex {
    private static final String STATUS_LINE = "%s %s";
    private final Application application;
    private final Map<String, Supplier<Resource>> index;
    private final ObjectMapper mapper;

    public ReflectionResourceIndex(Application application, ObjectMapper mapper) {
        this.application = application;
        this.mapper = mapper;
        this.index = createCachedIndex();
    }

    protected Map<String, Supplier<Resource>> createCachedIndex() {
        Map<String, Supplier<Resource>> table = new HashMap<>();
        String baseUri = getBasePath();
        for (Class<?> resourceClass : application.getClasses()) {
            Path resourcePath = resourceClass.getAnnotation(Path.class);
            if (resourcePath != null) {
                String path = baseUri + cleanedPath(resourcePath.value());
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
                            Path methodPath = method.getAnnotation(Path.class);
                            String childPath = path;
                            if (methodPath != null) {
                                childPath += cleanedPath(methodPath.value());
                            }
                            String statusLine = String.format(STATUS_LINE, methodName, childPath);
                            index.put(statusLine, () -> new ReflectionResource(application, method, mapper));
                        }
                    }
                }
            }
        }
        return table;
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

    protected String cleanedPath(String path) {
        String base = path;
        if (!base.startsWith("/")) {
            base = "/" + path;
        }
        return base;
    }

    @Override
    public Optional<Resource> findResource(FullHttpRequest request) {
        String statusLine = String.format(STATUS_LINE, request.getContext().getMethod(),
                request.getContext().getPath());
        if (index.containsKey(statusLine)) {
            return Optional.of(index.get(statusLine).get());
        } else {
            return Optional.empty();
        }
    }

}
