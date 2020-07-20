package me.vshnv.persistanceframework

import me.vshnv.persistanceframework.sql.SQLRepository
import me.vshnv.persistanceframework.sql.connector.SQLiteConnector
import java.io.File
import java.util.*

fun main() {


    val connector = SQLiteConnector(File("myFolder"), "test")

    val persistentRepo: Repository<UUID, TestData> = SQLRepository(
            UUID::class.java,
            TestData::class.java,
            connector,
            "TestTable")

    persistentRepo.with(UUID.randomUUID()) {

    }

}