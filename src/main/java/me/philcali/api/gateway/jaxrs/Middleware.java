package me.philcali.api.gateway.jaxrs;

import java.util.function.Function;

public class Middleware implements Function<FullHttpRequest, FullHttpResponse> {
    private final ResourceIndex index;

    public Middleware(ResourceIndex index) {
        this.index = index;
    }

    @Override
    public FullHttpResponse apply(FullHttpRequest request) {
        return index.findResource(request).map(res -> res.apply(request))
                .orElseGet(() -> new FullHttpResponse().withStatus(404).withErrorMessage("Resource not found."));
    }
}
