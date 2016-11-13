package me.philcali.api.gateway.jaxrs;

import java.util.function.Function;

public interface Resource extends Function<FullHttpRequest, FullHttpResponse> {
}
