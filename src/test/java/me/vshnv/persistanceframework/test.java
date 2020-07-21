package me.vshnv.persistanceframework;


import me.vshnv.persistanceframework.sql.ConnectorDriverNotFoundException;
import me.vshnv.persistanceframework.sql.SQLDriver;
import me.vshnv.persistanceframework.sql.SQLRepository;
import me.vshnv.persistanceframework.sql.connector.SQLConnector;
import me.vshnv.persistanceframework.sql.connector.SQLiteConnector;

import java.io.File;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class test {
    public static void main(String[] args) {
        SQLConnector connector = new SQLConnector(SQLDriver.MYSQL, "wildmines", "localhost", "3306", "root", "19BCG10015");
        Repository<UUID, TestData> persistentRepo = new SQLRepository(UUID.class, TestData.class, connector, "TestTable");
        UUID id = UUID.nameUUIDFromBytes("SIMON".getBytes());
        TestData personA = new TestData(id,"Simon", 30, true);


        persistentRepo.insert(personA, ()-> {
            System.out.print("Inserted");
            onInsert(persistentRepo, id);
            return null;
        });
    }

    private static void onInsert(Repository<UUID, TestData> persisitantRepo, UUID id) {
        persisitantRepo.with(id, person -> {
            System.out.println(person.getName());
            System.out.println(person.getAge());
            System.out.println(person.isStudent());
        });
    }
}
