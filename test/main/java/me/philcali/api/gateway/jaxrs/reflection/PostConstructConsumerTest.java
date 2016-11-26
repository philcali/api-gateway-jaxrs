package me.philcali.api.gateway.jaxrs.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import me.philcali.api.gateway.jaxrs.model.TestObject;

public class PostConstructConsumerTest {
    private TestObject testObj;

    @Before
    public void setUp() {
        testObj = new TestObject("Philip", 99);
    }

    @Test
    public void testAccept() {
        assertNull(testObj.getDescription());
        new PostConstructConsumer<TestObject>().accept(testObj);
        assertEquals("My name is Philip, and I'm 99 years old.", testObj.getDescription());
    }

}
