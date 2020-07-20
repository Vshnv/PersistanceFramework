package me.vshnv.persistanceframework

import com.github.benmanes.caffeine.cache.Cache
import com.google.gson.Gson
import me.vshnv.persistanceframework.annotations.PersistentKey
import me.vshnv.persistanceframework.annotations.PersistentProperty
import me.vshnv.persistanceframework.sql.SQLRepository
import me.vshnv.persistanceframework.sql.connector.SQLConnector
import org.omg.CORBA.Object
import java.lang.reflect.Field
import java.sql.ResultSet
import java.util.function.Consumer

operator fun <T> Consumer<T>.invoke(value: T) = accept(value)

operator fun <K, V> Cache<K, V>.set(key: K, value: V) = put(key,value)


val Field.persistantName: String
    get() =
        if (isAnnotationPresent(PersistentProperty::class.java))
            getAnnotation(PersistentProperty::class.java).name
        else  name
val Field.sqlType: String
    get() =
        when(type){
            (Short::class.java) -> "SHORT"
            (Int::class.java) -> "INT"
            (Long::class.java) -> "BIGINT"
            (Float::class.java) -> "FLOAT"
            (Double::class.java) -> "DOUBLE"
            (Boolean::class.java) -> "BOOLEAN"
            (Byte::class.java) -> "BYTE"
            else -> if (isAnnotationPresent(PersistentKey::class.java)) "VARCHAR" else "TEXT"
        } + if (isAnnotationPresent(PersistentKey::class.java)) "(100)" else ""

fun ResultSet.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    val colCount = metaData.columnCount
    for (i in 1..colCount) {
        map[metaData.getColumnName(i)] = getObject(i)
    }
    return map
}







//Json utils
private val gson = Gson()

fun <T> T.toJSON(): String {
    return gson.toJson(this)
}

fun <T> String.parseJSON(clazz: Class<T>): T {
    return gson.fromJson(this, clazz)
}