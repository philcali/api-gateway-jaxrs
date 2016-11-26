package me.philcali.api.gateway.jaxrs.exception;

public class ResourceException extends RuntimeException {
    public ResourceException(Throwable t) {
        super(t);
    }

    @Override
    public String toString() {
        Throwable bottom = getCause();
        while (bottom.getCause() != null) {
            bottom = bottom.getCause();
        }
        return bottom.getMessage();
    }
}
