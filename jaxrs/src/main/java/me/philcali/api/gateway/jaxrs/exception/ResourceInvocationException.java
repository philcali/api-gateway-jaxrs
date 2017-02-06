package me.philcali.api.gateway.jaxrs.exception;

public class ResourceInvocationException extends ResourceException {
    /**
     *
     */
    private static final long serialVersionUID = -3514631751830659232L;

    public ResourceInvocationException(Throwable t) {
        super(t);
    }
}
