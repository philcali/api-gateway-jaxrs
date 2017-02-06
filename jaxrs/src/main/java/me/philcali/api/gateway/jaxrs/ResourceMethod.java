package me.philcali.api.gateway.jaxrs;

import java.util.function.Function;
import java.util.function.Predicate;

public interface ResourceMethod extends Function<FullHttpRequest, FullHttpResponse>, Predicate<FullHttpRequest> {
    String getMethod();
    String getPath();
    Resource getResource();

    @Override
    default boolean test(FullHttpRequest request) {
        return request.getContext().getMethod().equals(getMethod()) && request.getContext().getPath().equals(getPath());
    }
}
