package me.philcali.api.gateway.jaxrs;

import java.util.function.Function;

import me.philcali.api.gateway.jaxrs.exception.ResourceException;

public class Middleware implements Function<FullHttpRequest, FullHttpResponse> {
    private final ResourceIndex index;

    public Middleware(ResourceIndex index) {
        this.index = index;
    }

    @Override
    public FullHttpResponse apply(FullHttpRequest request) {
        return index.findMethod(request).map(method -> {
            try {
                return method.apply(request);
            } catch (ResourceException rex) {
                return errorResponse(rex);
            }
        }).orElseGet(this::missingResponse);
    }

    protected FullHttpResponse errorResponse(ResourceException ex) {
        return new FullHttpResponse().withStatus(500).withErrorMessage(ex.toString());
    }

    protected FullHttpResponse missingResponse() {
        return new FullHttpResponse().withStatus(404).withErrorMessage("Resource not found.");
    }
}
