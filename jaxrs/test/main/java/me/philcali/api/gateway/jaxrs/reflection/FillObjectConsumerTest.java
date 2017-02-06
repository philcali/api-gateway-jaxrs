package me.philcali.api.gateway.jaxrs.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.ws.rs.core.Application;

import org.junit.Before;
import org.junit.Test;

import me.philcali.api.gateway.jaxrs.model.ResourceApplication;
import me.philcali.api.gateway.jaxrs.model.ResourceModel;
import me.philcali.api.gateway.jaxrs.model.ResourceModel.Configuration;
import me.philcali.api.gateway.jaxrs.model.TestObject;

public class FillObjectConsumerTest {
    private Configuration config;

    @Before
    public void setUp() {
        config = new Configuration();
        config.setAddress("localhost");
        config.setPort(80);
    }

    @Test
    public void testAccept() {
        Application application = new ResourceApplication(config);
        ResourceModel model = new ResourceModel(config);
        assertNull(model.getPerson());
        new FillObjectConsumer<>(application.getProperties()).accept(model);
        assertEquals(new TestObject("Philip", 99), model.getPerson());
    }

}
