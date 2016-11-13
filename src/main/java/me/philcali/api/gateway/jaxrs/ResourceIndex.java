package me.philcali.api.gateway.jaxrs;

import java.util.Optional;

public interface ResourceIndex {
    public Optional<Resource> findResource(FullHttpRequest request);
}
