package me.philcali.api.gateway.jaxrs.binding;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant.VariantListBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

public class JaxrsRuntimeDelegate extends RuntimeDelegate {

    @Override
    public UriBuilder createUriBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResponseBuilder createResponseBuilder() {
        return new JaxrsResponse.RespBuilder();
    }

    @Override
    public VariantListBuilder createVariantListBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T createEndpoint(Application application, Class<T> endpointType)
            throws IllegalArgumentException, UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Builder createLinkBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

}
