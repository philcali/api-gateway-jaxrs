package me.philcali.api.gateway.jaxrs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
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
            throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object[] args = new Object[method.getParameterCount()];
        for (int i = 0; i <= method.getParameterCount(); i++) {
            final int index = i;
            Parameter param = method.getParameters()[i];
            getParameterizedValue(param, request).ifPresent(value -> args[index] = value);
            if (FullHttpRequest.class.isAssignableFrom(param.getType())) {
                args[i] = request;
            } else {
                String json = mapper.writeValueAsString(request.getBody());
                Object subType = mapper.readValue(json, param.getType());
                args[i] = subType;
            }
        }
        return method.invoke(instance, args);
    }

    @Override
    public FullHttpResponse apply(FullHttpRequest request) {
        Class<?> wrapperClass = method.getDeclaringClass();
        try {
            Object instance = wrapperClass.newInstance();
            fillObjectContext(wrapperClass, instance);
            postConstructor(wrapperClass, instance);
            Object retVal = invokeMethod(instance, request);
            FullHttpResponse response = new FullHttpResponse();
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
        } catch (InstantiationException | IllegalAccessException ie) {
            throw new ResourceCreationException(ie);
        } catch (IllegalArgumentException | InvocationTargetException | IOException e) {
            throw new ResourceInvocationException(e);
        }
    }
}
