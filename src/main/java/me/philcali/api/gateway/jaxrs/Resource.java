package me.philcali.api.gateway.jaxrs;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public interface Resource {
    // TODO: Set<Resource> getResources();
    Set<ResourceMethod> getMethods();
    Supplier<?> getSupplier();
    String getPath();
    default Optional<ResourceMethod> findMethod(FullHttpRequest request) {
        return getMethods().stream().filter(method -> method.test(request)).findFirst();
    }
}
