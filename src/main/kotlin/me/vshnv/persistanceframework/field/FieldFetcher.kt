package me.vshnv.persistanceframework.field

import me.vshnv.persistanceframework.Persistable
import java.lang.reflect.Field
import java.lang.reflect.Modifier

internal object FieldFetcher {
    private val map:MutableMap<Class<*>, PersistentData> = mutableMapOf()

    fun <T, K> registerClass(type: Class<T>, keyType: Class<K>) where T: Persistable<K> {
        if (map.contains(type))return
        var key: Field = type.declaredFields.filter {
            it.javaClass == keyType.javaClass
        }.first {
            it.isAnnotationPresent(TODO("Add annotations"))
        }

        val nonKeyFields = type.declaredFields.filter { it != key && !Modifier.isTransient(it.modifiers)}
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