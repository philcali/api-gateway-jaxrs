package me.philcali.api.gateway.jaxrs.reflection;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.philcali.api.gateway.jaxrs.FullHttpRequest;
import me.philcali.api.gateway.jaxrs.FullHttpResponse;
import me.philcali.api.gateway.jaxrs.Resource;
import me.philcali.api.gateway.jaxrs.exception.ResourceInvocationException;

public class ReflectionResource implements Resource {
    private final Application application;
    private final Method method;
    private final ObjectMapper mapper;
    private final Supplier<?> supplier;

    public ReflectionResource(Application application, Method method, ObjectMapper mapper, Supplier<?> supplier) {
        this.application = application;
        this.method = method;
        this.mapper = mapper;
        this.supplier = supplier;
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

    protected Object invokeMethod(Object instance, FullHttpRequest request)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object[] args = new Object[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            final int index = i;
            Parameter param = method.getParameters()[i];
            getParameterizedValue(param, request).ifPresent(value -> args[index] = value);
            if (FullHttpRequest.class.isAssignableFrom(param.getType())) {
                args[i] = request;
            } else {
                args[i] = Optional.ofNullable(param)
                        .filter(p -> p.getAnnotation(Context.class) != null)
                        .flatMap(p -> ReflectionUtils.findContextType(application.getProperties(), p))
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

    protected Optional<String> getContentType(FullHttpRequest request) {
        Optional<String> accept = Optional.ofNullable(request.getParams().get("Accept"));
        return Optional.ofNullable(method.getAnnotation(Produces.class)).flatMap(produces -> {
            return Arrays.stream(produces.value()).filter(media -> accept.filter(media::equals).isPresent())
                    .findFirst();
        });
    }

    @Override
    public FullHttpResponse apply(final FullHttpRequest request) {
        try {
            final Object instance = supplier.get();
            final Object retVal = invokeMethod(instance, request);
            final FullHttpResponse response = new FullHttpResponse();
            getContentType(request).ifPresent(contentType -> response.addHeader("Content-Type", contentType));
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
