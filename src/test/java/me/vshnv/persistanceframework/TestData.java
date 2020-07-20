package me.vshnv.persistanceframework;

import java.util.UUID;

public class TestData implements Persistable<UUID> {
    private UUID id;
    public String name;
    private AnotherData data;
    public Boolean testz;
    @Override
    public void init(UUID uuid) {

    }

    public UUID getKey() {
        return getKey();
    }
}
