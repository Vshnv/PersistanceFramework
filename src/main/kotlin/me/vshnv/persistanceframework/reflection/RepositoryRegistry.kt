package me.vshnv.persistanceframework.reflection

import me.vshnv.persistanceframework.Repository

internal object RepositoryRegistry {
    val repoMap: MutableMap<String, Repository<*, *>> = mutableMapOf()
    fun register(repo: Repository<*, *>) {

    }
    fun unregister(repo: Repository<*, *>) {

    }
    fun unregister(name: String) {
        repoMap.remove(name)
    }
    fun get(name: String): Repository<*, *>? {
        return repoMap[name]
    }
}