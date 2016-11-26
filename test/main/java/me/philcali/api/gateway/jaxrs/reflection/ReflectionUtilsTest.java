package me.philcali.api.gateway.jaxrs.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import me.philcali.api.gateway.jaxrs.model.TestObject;

public class ReflectionUtilsTest {
    private Map<String, Object> context;
    private Parameter name;
    private Parameter age;

    @Before
    public void setUp() throws NoSuchMethodException, SecurityException {
        context = new HashMap<>();
        context.put("name", "Philip Cali");
        context.put("age", 99);

        Constructor<TestObject> construct = TestObject.class.getConstructor(String.class, Integer.TYPE);
        name = construct.getParameters()[0];
        age = construct.getParameters()[1];
    }

    @Test
    public void testFindContextTypeName() {
        Optional<Object> nameVal = ReflectionUtils.findContextType(context, name);
        assertTrue(nameVal.isPresent());
        assertEquals("Philip Cali", nameVal.get());
    }

    @Test
    public void testFindContextTypeAge() {
        Optional<Object> ageVal = ReflectionUtils.findContextType(context, age);
        assertTrue(ageVal.isPresent());
        assertEquals(99, ageVal.get());
    }
}
