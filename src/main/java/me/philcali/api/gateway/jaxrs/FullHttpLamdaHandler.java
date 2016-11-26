package me.philcali.api.gateway.jaxrs;

import javax.ws.rs.core.Application;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.philcali.api.gateway.jaxrs.reflection.ReflectionResourceIndex;

public abstract class FullHttpLamdaHandler implements RequestHandler<FullHttpRequest, FullHttpResponse> {

    protected abstract Application getApplication();

    @Override
    public FullHttpResponse handleRequest(FullHttpRequest request, Context context) {
        Application application = getApplication();
        Middleware middleware = new Middleware(new ReflectionResourceIndex(application, new ObjectMapper()));
        return middleware.apply(request);
    }
}