package me.vshnv.persistanceframework.reflection

import me.vshnv.persistanceframework.parseJSON
import me.vshnv.persistanceframework.persistantName
import sun.reflect.ReflectionFactory
import java.lang.reflect.Modifier
import kotlin.reflect.typeOf


private val reflectionFactory = ReflectionFactory.getReflectionFactory()

internal class ObjectMapper<T>(private val fieldMap: Map<String,Any>,private val clazz: Class<T>) {

    fun mapToObject(): T {
        val result = createTypedObject()
        clazz.declaredFields.filterNot { Modifier.isTransient(it.modifiers) }.forEach { field ->
            field.isAccessible = true
            when {
                field.type == (Int::class.java) || field.type == (Long::class.java) ||
                        field.type == (Float::class.java) || field.type == (Double::class.java) ||
                field.type == (String::class.java) ||
                field.type == (Boolean::class.java) ||
                field.type == (Byte::class.java)-> {
                    field.set(result, fieldMap[field.persistantName])
                }
                else -> field.set(result, (fieldMap[field.persistantName] as String).parseJSON(field.type))

            }
        }
        return result
    }

    private fun createTypedObject(): T {
        val constr = Any::class.java.getDeclaredConstructor()
        val openConstructor = reflectionFactory.newConstructorForSerialization(
            clazz, constr
        )
        return clazz.cast(openConstructor.newInstance())
    }
}