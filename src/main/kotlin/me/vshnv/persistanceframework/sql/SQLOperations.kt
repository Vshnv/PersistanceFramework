package me.vshnv.persistanceframework.sql

import me.vshnv.persistanceframework.*
import me.vshnv.persistanceframework.reflection.FieldFetcher
import me.vshnv.persistanceframework.reflection.ObjectMapper
import me.vshnv.persistanceframework.reflection.PersistentData
import me.vshnv.persistanceframework.sql.connector.ISQLConnector
import java.lang.reflect.Field
import java.sql.Connection
import java.sql.PreparedStatement

class SQLUpdater<K, V>(private val table: String, private val connector: ISQLConnector, private val keyClass: Class<K>, private val valueClass: Class<V>) : Updater<K, V>() where V: Persistable<K> {
    override fun pushUpdates(updateSet: Set<V>, onComplete: () -> Unit) {
        val data: PersistentData = FieldFetcher[valueClass] ?: throw java.lang.IllegalArgumentException("Unregistered persistant class!")
        val fields = data.fields
        connector.connect{conn->
            val statement = data.prepareUpdateStatement(conn, table)
            conn.autoCommit = false
            updateSet.forEach {value->
                fields.forEachIndexed { fIndex, field ->
                    statement.setVariable(fIndex + 1, field, value) //+1 since prepared statement index starts at 1
                }
                statement.setVariable(fields.size + 1, data.key, value)
                statement.addBatch()
            }
            statement.executeUpdate()
            conn.commit()
            conn.autoCommit = true
        }
    }
}

class SQLInserter<K, V>(private val table: String, private val connector: ISQLConnector, private val keyClass: Class<K>, private val valueClass: Class<V>) : Inserter<K, V>() where V: Persistable<K> {


    override fun insert(vararg values: V, onInsert: () -> Unit) {
        val data: PersistentData = FieldFetcher[valueClass] ?: throw java.lang.IllegalArgumentException("Unregistered persistant class!")
        val fields = data.fields
        connector.connect { conn->
            val statement = data.prepareInsertStatement(conn, table)
            conn.autoCommit = false
            values.forEachIndexed {outerIndex, value ->
                fields.forEachIndexed { fIndex, field ->
                    statement.setVariable(fIndex + 1, field, value) //+1 since prepared statement index starts at 1
                }
                if (outerIndex < values.size - 1) {
                    statement.addBatch()
                }
            }
            statement.executeUpdate()
            conn.commit()
            conn.autoCommit = true

            onInsert()
        }

    }


}


class SQLFetcher<K, V>(private val table: String, private val connector: ISQLConnector, private val keyClass: Class<K>, private val valueClass: Class<V>) : Fetcher<K, V>() where V: Persistable<K> {

    override fun fetch(key: K, onFetch: (V) -> Unit) {
        val data: PersistentData = FieldFetcher[valueClass] ?: return;
        connector.connect {conn ->
            val stmt = data.prepareSelectStatement(conn, table);
            stmt.setString(1, key.toJSON())
            val result = stmt.executeQuery()
            if (result.next()) {
                val mapper = ObjectMapper(result.toMap(), valueClass)
                onFetch(mapper.mapToObject())
            }else {

            }
        }
    }
}

class SQLDeleter<K, V>(private val table: String, private val connector: ISQLConnector, private val keyClass: Class<K>, private val valueClass: Class<V>) : Deleter<K, V>() where V: Persistable<K> {

    override fun delete(key: K, onDelete: () -> Unit) {
        val data: PersistentData = FieldFetcher[valueClass] ?: return
        connector.connect {conn->
            val stmt = data.prepareDeleteStatement(conn, table)
            stmt.setString(1, key.toJSON())
            stmt.executeUpdate()
            onDelete()
        }
    }

    override fun delete(value: V, onDelete: () -> Unit) {
        val data: PersistentData = FieldFetcher[valueClass] ?: return
        val key: K = data.key.get(value) as K
        delete(key, onDelete)
    }
}

//Local utility extensions

private fun PersistentData.prepareInsertStatement(conn: Connection, table: String): PreparedStatement {
    val columns = fields.joinToString( separator = "," , transform = {it.persistantName})
    val placeholders = CharArray(fields.size) { i -> '?'}.joinToString(separator=",");
    val query = "INSERT INTO $table ($columns) VALUES ($placeholders) ON DUPLICATE KEY UPDATE ${key.persistantName}=${key.persistantName}"
    return conn.prepareStatement(query)
}

private fun PersistentData.prepareSelectStatement(conn: Connection, table: String): PreparedStatement {
    val columns = fields.joinToString( separator = "," , transform = {it.persistantName})
    val query = "SELECT $columns FROM $table WHERE ${this.key.persistantName}=? LIMIT 0, 1"
    return conn.prepareStatement(query)
}

private fun PersistentData.prepareUpdateStatement(conn: Connection, table: String): PreparedStatement {
    val keyValPairs = fields.joinToString( separator = "," , transform = {"${it.persistantName}=?"})
    val keyName = key.persistantName
    val query = "UPDATE $table SET $keyValPairs WHERE $keyName=?"
    return conn.prepareStatement(query)
}

private fun PersistentData.prepareDeleteStatement(conn: Connection, table: String): PreparedStatement {
    val keyName = key.persistantName
    val query = "DELETE FROM $table WHERE $keyName=?"
    return conn.prepareStatement(query)
}

private fun <T> PreparedStatement.setVariable(index: Int, field: Field, instance: T) {
    val type = field.type
    field.isAccessible = true

    if (type.interfaces.contains(Persistable::class.java)) {
        val foreignKey = FieldFetcher[type]?.key?.get(instance) ?: throw IllegalArgumentException("Unregistered Persistable Class: $type")
        when (foreignKey) {
            is Double,
            is Float,
            is Boolean,
            is String,
            is Byte -> setObject(index, foreignKey)
            else -> {
                setString(index, foreignKey.toJSON())
            }
        }
    } else {
        when (val variable = field[instance]) {
            is Double,
            is Float,
            is Boolean,
            is String,
            is Byte -> setObject(index, variable)
            else -> {
                setString(index, variable.toJSON())
            }
        }
    }
}



