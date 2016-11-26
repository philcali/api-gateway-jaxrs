package me.philcali.api.gateway.jaxrs.model;

import java.util.Objects;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("tests")
public class ResourceModel {
    public static class Configuration {
        private int port;
        private String address;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Override
        public int hashCode() {
            return Objects.hash(port, address);
        }

        @Override
        public boolean equals(Object obj) {
            if (Objects.isNull(obj) || !(obj instanceof Configuration)) {
                return false;
            }
            Configuration config = (Configuration) obj;
            return Objects.equals(port, config.getPort()) && Objects.equals(address, config.getAddress());
        }
    }

    private final Configuration config;

    @Context
    private TestObject testObject;

    public ResourceModel(Configuration config) {
        this.config = config;
    }

    @GET
    @Produces("application/json")
    public Response getTest() {
        return Response.ok(config).build();
    }

    @GET
    public TestObject getPerson() {
        return testObject;
    }

    @GET
    @Path("/echo")
    public Response echo(@QueryParam("phrase") String phrase) {
        return Response.ok(phrase).build();
    }
}
