package me.vshnv.persistanceframework

import java.util.*
import java.util.function.Consumer

abstract class Updater<K, V>() where V: Persistable<K> {
    private val updateSet: MutableSet<V> = mutableSetOf()

    fun notifyDelete(value: V) {
        updateSet.remove(value)
    }

    fun notifyDelete(key: K) {
        updateSet.filter { TODO("ADD CLASS TO FETCH KEY") }.forEach {
            updateSet.remove(it)
        }
    }


    fun update(vararg values: V) {
        updateSet.addAll(values)
    }

    fun commitChanges() {
        pushUpdates(updateSet) {
            updateSet.clear()
        }
    }
    protected abstract fun pushUpdates(updateSet: Set<V>, onComplete: () -> Unit)
}

abstract class Inserter<K, V>() where V: Persistable<K> {
    abstract fun insert(vararg values: V, onInsert: () -> Unit)
}



abstract class Fetcher<K, V>() where V: Persistable<K> {
    abstract fun fetch(key: K, onFetch: (V) -> Unit)
}

abstract class Deleter<K, V>() where V: Persistable<K> {
    abstract fun delete(key: K, onDelete: () -> Unit)
    abstract fun delete(value: V, onDelete: () -> Unit)
}