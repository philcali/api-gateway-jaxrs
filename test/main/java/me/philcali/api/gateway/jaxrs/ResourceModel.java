package me.philcali.api.gateway.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
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
    }

    private final Configuration config;

    public ResourceModel(Configuration config) {
        this.config = config;
    }

    @GET
    public Response getTest() {
        return Response.ok(config).build();
    }

    @GET
    @Path("/echo")
    public Response echo(@QueryParam("phrase") String phrase) {
        return Response.ok(phrase).build();
    }
}
