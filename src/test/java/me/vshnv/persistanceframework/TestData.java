package me.vshnv.persistanceframework;

import me.vshnv.persistanceframework.annotations.PersistentKey;
import me.vshnv.persistanceframework.annotations.PersistentProperty;

import java.util.UUID;

public class TestData implements Persistable<UUID> {
    private @PersistentKey UUID id;
    private String name;
    private int age;
    private boolean student;

    public TestData(UUID id, String name, int age, boolean student) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.student = student;
    }

    @Override
    public void init(UUID uuid) {

    }

    public String getName() {
        return name;
    }
    public int getAge() {
        return age;
    }
    public boolean isStudent() {
        return student;
    }
}

