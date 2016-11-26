package me.philcali.api.gateway.jaxrs;

import java.util.Optional;
import java.util.Set;

public interface ResourceIndex {
    public Optional<Resource> findResource(FullHttpRequest request);
    public Set<String> getApplicationPaths();
}
