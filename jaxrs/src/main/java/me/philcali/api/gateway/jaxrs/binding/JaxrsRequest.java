package me.philcali.api.gateway.jaxrs.binding;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Variant;

import me.philcali.api.gateway.jaxrs.FullHttpRequest;

public class JaxrsRequest implements Request {
    private final FullHttpRequest request;

    public JaxrsRequest(final FullHttpRequest request) {
        this.request = request;
    }

    @Override
    public ResponseBuilder evaluatePreconditions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResponseBuilder evaluatePreconditions(final EntityTag arg0) {
        return null;
    }

    @Override
    public ResponseBuilder evaluatePreconditions(Date arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResponseBuilder evaluatePreconditions(Date arg0, EntityTag arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMethod() {
        return request.getContext().getMethod();
    }

    @Override
    public Variant selectVariant(List<Variant> arg0) {
        return arg0.stream().findFirst().orElse(null);
    }
}
