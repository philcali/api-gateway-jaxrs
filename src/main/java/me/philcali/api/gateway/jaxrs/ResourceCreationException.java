package me.philcali.api.gateway.jaxrs;

public class ResourceCreationException extends RuntimeException {
    private static final long serialVersionUID = 92347563951L;

    public ResourceCreationException(Throwable t) {
        super(t);
    }
}
