package me.vshnv.persistanceframework.reflection

import me.vshnv.persistanceframework.Persistable
import me.vshnv.persistanceframework.annotations.PersistentKey
import me.vshnv.persistanceframework.annotations.PersistentProperty
import java.lang.IllegalArgumentException
import java.lang.reflect.Field
import java.lang.reflect.Modifier

internal object FieldFetcher {
    private val map:MutableMap<Class<*>, PersistentData> = mutableMapOf()

    fun <T, K> registerClass(type: Class<T>, keyType: Class<K>) where T: Persistable<K> {
        if (map.contains(type))return
        var key: Field = type.declaredFields.filter {
            it.type == keyType
        }.firstOrNull {
            it.isAnnotationPresent(PersistentKey::class.java)
        } ?: throw IllegalArgumentException("No fields marked as Persistent key")

        val nonKeyFields = type.declaredFields.filter {!Modifier.isTransient(it.modifiers)}
        nonKeyFields.forEach { it.isAccessible = true }
        map[type] = PersistentData(nonKeyFields, key)

    }

    operator fun <T> get(type: Class<T>): PersistentData? {
        return map[type]
    }

}


data class PersistentData(internal val fields: List<Field>,internal val key: Field) {
    init {
        fields.forEach { it.isAccessible = true }
    }
}