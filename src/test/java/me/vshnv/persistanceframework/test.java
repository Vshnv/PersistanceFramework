package me.vshnv.persistanceframework;


import me.vshnv.persistanceframework.sql.ConnectorDriverNotFoundException;
import me.vshnv.persistanceframework.sql.SQLRepository;
import me.vshnv.persistanceframework.sql.connector.SQLiteConnector;

import java.io.File;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class test {
    public static void main(String[] args) throws ConnectorDriverNotFoundException, SQLException, InterruptedException {
        SQLiteConnector connector = new SQLiteConnector(new File("myFolder"), "test");
        Repository<UUID, TestData> persistentRepo = new SQLRepository(UUID.class, TestData.class, connector, "TestTable");
        persistentRepo.with(UUID.randomUUID(), (data)-> {

        });
    }
}
