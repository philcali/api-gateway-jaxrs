package me.philcali.api.gateway.jaxrs.model;

import javax.inject.Singleton;
import javax.ws.rs.GET;

@Singleton
public class SubResourceModel {
    private final TestObject person;

    public SubResourceModel(TestObject person) {
        this.person = person;
    }

    @GET
    public TestObject getPerson() {
        return person;
    }
}
