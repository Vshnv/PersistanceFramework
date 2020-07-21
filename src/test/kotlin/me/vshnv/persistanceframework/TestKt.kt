package me.vshnv.persistanceframework

import kotlinx.coroutines.runBlocking
import me.vshnv.persistanceframework.sql.ConnectorDriverNotFoundException
import me.vshnv.persistanceframework.sql.SQLDriver
import me.vshnv.persistanceframework.sql.SQLRepository
import me.vshnv.persistanceframework.sql.connector.SQLConnector
import java.sql.SQLException
import java.util.*

@Throws(ConnectorDriverNotFoundException::class, SQLException::class, InterruptedException::class)
fun main() = runBlocking {
    val connector = SQLConnector(SQLDriver.MYSQL, "wildmines", "localhost", "3306", "root", "19BCG10015")

    val persistentRepo: Repository<UUID, TestData> = SQLRepository(connector, "TestTable")

    val id = UUID.nameUUIDFromBytes("SIMON".toByteArray())
    val personA = TestData(id, "Simon", 30, true)

    fun onInsert() {
        persistentRepo.with(id) { person ->
            println(person.name)
            println(person.age)
            println(person.isStudent)
        }
    }

    persistentRepo.insert(personA) {
        print("Inserted")
        onInsert();
    }
}