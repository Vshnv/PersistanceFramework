package me.vshnv.persistanceframework.sql.connector

import java.sql.Connection
import java.util.function.Consumer

interface ISQLConnector {
    fun connect(operations: suspend (Connection) -> Unit)
}
