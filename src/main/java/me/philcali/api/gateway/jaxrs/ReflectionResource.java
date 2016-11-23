package me.philcali.api.gateway.jaxrs;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ReflectionResource implements Resource {
    private final Application application;
    private final Method method;
    private final ObjectMapper mapper;

    public ReflectionResource(Application application, Method method, ObjectMapper mapper) {
        this.application = application;
        this.method = method;
        this.mapper = mapper;
    }

    protected Optional<String> getParameterizedValue(Parameter param, FullHttpRequest request) {
        String returnVal = null;
        PathParam path = param.getAnnotation(PathParam.class);
        if (path != null) {
            returnVal = request.getParams().get(path.value());
        }
        QueryParam query = param.getAnnotation(QueryParam.class);
        if (query != null) {
            returnVal = request.getParams().get(query.value());
        }
        HeaderParam header = param.getAnnotation(HeaderParam.class);
        if (header != null) {
            returnVal = request.getParams().get(header.value());
        }
        return Optional.ofNullable(returnVal);
    }

    protected Optional<Object> findContextType(Parameter param) {
        return application.getProperties().values().stream()
                .filter(value -> param.getType().isAssignableFrom(value.getClass())).findFirst();
    }

    protected void fillObjectContext(Class<?> wrapperClass, Object instance) {
        Arrays.stream(wrapperClass.getFields()).filter(f -> f.getAnnotation(Context.class) != null).forEach(f -> {
            application.getProperties().values().stream()
            .filter(value -> f.getType().isAssignableFrom(value.getClass())).forEach(value -> {
                try {
                    f.setAccessible(true);
                    f.set(instance, value);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new ResourceCreationException(e);
                }
            });
        });
    }

    protected void postConstructor(Class<?> wrapperClass, Object instance) {
        Arrays.stream(wrapperClass.getMethods()).filter(m -> m.getAnnotation(PostConstruct.class) != null)
        .forEach(m -> {
            try {
                m.invoke(instance);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new ResourceCreationException(e);
            }
        });
    }

    protected Object invokeMethod(Object instance, FullHttpRequest request)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object[] args = new Object[method.getParameterCount()];
        for (int i = 0; i <= method.getParameterCount(); i++) {
            final int index = i;
            Parameter param = method.getParameters()[i];
            getParameterizedValue(param, request).ifPresent(value -> args[index] = value);
            if (FullHttpRequest.class.isAssignableFrom(param.getType())) {
                args[i] = request;
            } else {
                args[i] = Optional.ofNullable(param)
                        .filter(p -> p.getAnnotation(Context.class) != null)
                        .flatMap(this::findContextType)
                        .orElseGet(() -> {
                            try {
                                String json = mapper.writeValueAsString(request.getBody());
                                return mapper.readValue(json, param.getType());
                            } catch (IOException ie) {
                                throw new ResourceInvocationException(ie);
                            }
                        });
            }
        }
        return method.invoke(instance, args);
    }

    protected Object constructObject(Class<?> wrapperClass) {
        return Arrays.stream(wrapperClass.getConstructors()).sorted(new Comparator<Constructor<?>>() {
            @Override
            public int compare(Constructor<?> constrA, Constructor<?> constrB) {
                return Integer.compare(constrA.getParameterCount(), constrB.getParameterCount());
            }
        }).findFirst().map(construct -> {
            final Object[] params = new Object[construct.getParameterCount()];
            for (int i = 0; i < construct.getParameterCount(); i++) {
                final int index = i;
                Parameter param = construct.getParameters()[i];
                findContextType(param).ifPresent(value -> params[index] = value);
            }
            try {
                return (Object) construct.newInstance(params);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new ResourceCreationException(e);
            }
        }).orElseGet(() -> {
            try {
                return wrapperClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ResourceCreationException(e);
            }
        });
    }

    @Override
    public FullHttpResponse apply(final FullHttpRequest request) {
        final Class<?> wrapperClass = method.getDeclaringClass();
        try {
            final Object instance = constructObject(wrapperClass);
            fillObjectContext(wrapperClass, instance);
            postConstructor(wrapperClass, instance);
            final Object retVal = invokeMethod(instance, request);
            final FullHttpResponse response = new FullHttpResponse();
            if (retVal instanceof Response) {
                Response resp = (Response) retVal;
                response.withStatus(resp.getStatus());
                resp.getStringHeaders().forEach((k, v) -> response.addHeaderValues(k, v));
                if (resp.hasEntity()) {
                    response.withBody(resp.getEntity());
                }
            } else if (retVal != null) {
                response.withStatus(200).withBody(retVal);
            } else {
                response.withStatus(204);
            }
            return response;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ResourceInvocationException(e);
        }
    }
}
