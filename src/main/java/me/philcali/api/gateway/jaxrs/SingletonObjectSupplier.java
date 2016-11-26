package me.philcali.api.gateway.jaxrs;

public class SingletonObjectSupplier<T> implements ObjectSupplier<T> {
    private final T object;

    public SingletonObjectSupplier(T object) {
        this.object = object;
    }

    @Override
    public T get() {
        return object;
    }
}
