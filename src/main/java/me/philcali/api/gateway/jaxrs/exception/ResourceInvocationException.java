package me.philcali.api.gateway.jaxrs.exception;

public class ResourceInvocationException extends ResourceException {
    public ResourceInvocationException(Throwable t) {
        super(t);
    }
}
