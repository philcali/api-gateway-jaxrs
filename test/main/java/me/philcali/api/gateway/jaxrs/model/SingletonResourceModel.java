package me.philcali.api.gateway.jaxrs.model;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import me.philcali.api.gateway.jaxrs.model.ResourceModel.Configuration;

@Path("/singleton")
public class SingletonResourceModel {
    private final Configuration config;

    public SingletonResourceModel(Configuration config) {
        this.config = config;
    }

    @GET
    public Configuration getConfiguration() {
        return config;
    }

    @POST
    public Configuration setConfiguration(Configuration config) {
        this.config.setPort(config.getPort());
        this.config.setAddress(config.getAddress());
        return this.config;
    }

    @Path("/person")
    public Class<SubResourceModel> getPersonModel() {
        return SubResourceModel.class;
    }
}
