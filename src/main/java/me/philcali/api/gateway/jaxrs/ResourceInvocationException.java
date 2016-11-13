package me.philcali.api.gateway.jaxrs;

public class ResourceInvocationException extends RuntimeException {
    private static final long serialVersionUID = 9475270491L;

    public ResourceInvocationException(Throwable t) {
        super(t);
    }
}
