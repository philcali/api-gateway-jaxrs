package me.philcali.api.gateway.jaxrs;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public interface Resource extends ResourceIndex {
    Set<ResourceMethod> getMethods();
    Supplier<?> getSupplier();

    default Set<ResourceMethod> getAllMethods() {
        Set<ResourceMethod> allMethods = new HashSet<>(getMethods());
        getResources().stream().map(resource -> resource.getAllMethods()).forEach(allMethods::addAll);
        return allMethods;
    }

    @Override
    default Optional<ResourceMethod> findMethod(FullHttpRequest request) {
        return getAllMethods().stream().filter(method -> method.test(request)).findFirst();
    }
}
