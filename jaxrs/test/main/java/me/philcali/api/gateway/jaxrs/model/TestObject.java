package me.philcali.api.gateway.jaxrs.model;

import java.util.Objects;

import javax.annotation.PostConstruct;

public class TestObject {
    private String name;
    private int age;
    private String description;

    public TestObject(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @PostConstruct
    public void init() {
        description = String.format("My name is %s, and I'm %d years old.", name, age);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj)) {
            return false;
        }
        TestObject person = (TestObject) obj;
        return Objects.equals(name, person.getName()) && Objects.equals(age, person.getAge());
    }
}
