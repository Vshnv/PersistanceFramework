package me.vshnv.persistanceframework.sql.connector

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.vshnv.persistanceframework.invoke
import me.vshnv.persistanceframework.sql.SQLDriver
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.util.function.Consumer

class SQLiteConnector(folder: File, dbName: String): ISQLConnector {
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        SQLDriver.SQLITE.loadDriver()
        folder.mkdirs()
    }


    private val jdbcUrl: String = String.format(
            SQLDriver.SQLITE.urlFormat,
            SQLDriver.SQLITE.driverName,
            File(folder, "$dbName.db").absolutePath
    )


    override fun connect(operations: suspend (Connection) -> Unit) {
        scope.launch {
            DriverManager.getConnection(jdbcUrl).use {
                operations(it)
            }
        }
    }

    override fun connectSync(operations: (Connection) -> Unit) {
        DriverManager.getConnection(jdbcUrl).use {
            operations(it)
        }
    }

}
