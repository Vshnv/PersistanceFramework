package me.vshnv.persistanceframework

import com.github.benmanes.caffeine.cache.Cache
import com.google.gson.Gson
import me.vshnv.persistanceframework.annotations.PersistentProperty
import java.lang.reflect.Field
import java.util.function.Consumer

operator fun <T> Consumer<T>.invoke(value: T) = accept(value)

operator fun <K, V> Cache<K, V>.set(key: K, value: V) = put(key,value)

fun Field.getPersistentName(): String {
    if (isAnnotationPresent(PersistentProperty::class.java)) {
        return getAnnotation(PersistentProperty::class.java).name;
    }
    return name
}


private val gson = Gson()

fun <T> T.toJSON(): String {
    return gson.toJson(this)
}

fun <T> String.parseJSON(clazz: Class<T>): T {
    return gson.fromJson(this, clazz)
}