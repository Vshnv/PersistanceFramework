package me.vshnv.persistanceframework.sql.connector

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.vshnv.persistanceframework.sql.SQLDriver
import java.sql.Connection
import java.util.function.Consumer

class SQLConnector constructor(driver: SQLDriver, databaseName: String, hostname: String, port: String, username: String, password: String): ISQLConnector {
    private val scope = CoroutineScope(Dispatchers.IO)



    init {
        if (driver == SQLDriver.SQLITE) throw IllegalArgumentException("Use SQLiteConnector while connecting with SQLite driver")
        driver.loadDriver()
    }




    private val config: HikariConfig = HikariConfig().apply {
        driverClassName = driver.driverClasspath
        addDataSourceProperty("cachePrepStmts" , "true")
        addDataSourceProperty("prepStmtCacheSize", "250")
        addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        isAutoCommit = true
        jdbcUrl = String.format(
                driver.urlFormat,
                driver.driverName,
                hostname, port,
                databaseName
        )
        this.username = username
        this.password = password
    }




    private var dataSource: HikariDataSource = HikariDataSource(config)






    override fun connect(operations: suspend (Connection) -> Unit) {
        scope.launch {
            dataSource.connection.use {
                operations(it)
            }
        }
    }
}
