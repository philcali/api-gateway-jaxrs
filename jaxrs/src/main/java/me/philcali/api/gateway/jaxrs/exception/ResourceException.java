package me.philcali.api.gateway.jaxrs.exception;

public class ResourceException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -5221323448982023585L;

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
