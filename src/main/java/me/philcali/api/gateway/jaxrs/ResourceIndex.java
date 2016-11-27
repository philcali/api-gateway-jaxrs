package me.philcali.api.gateway.jaxrs;

import java.util.Optional;
import java.util.Set;

public interface ResourceIndex {
    public Optional<ResourceMethod> findMethod(FullHttpRequest request);
    public Set<Resource> getResources();
}
