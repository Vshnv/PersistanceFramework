package me.vshnv.persistanceframework.sql

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import com.github.benmanes.caffeine.cache.RemovalListener
import kotlinx.coroutines.runBlocking
import me.vshnv.persistanceframework.*
import me.vshnv.persistanceframework.annotations.PersistentKey
import me.vshnv.persistanceframework.reflection.FieldFetcher
import me.vshnv.persistanceframework.reflection.PersistentData
import me.vshnv.persistanceframework.reflection.RepositoryRegistry
import me.vshnv.persistanceframework.sql.connector.ISQLConnector
import me.vshnv.persistanceframework.sql.connector.SQLConnector
import org.jetbrains.annotations.NotNull
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class SQLRepository<K, V : Persistable<K>>
@JvmOverloads constructor(
        keyType: Class<K>,
        valueType: Class<V>,
        connector: ISQLConnector,
        table: String, evictionMinutes: Long = 20) : Repository<K, V>, Relational<V> {

    init {
        FieldFetcher.registerClass(valueType, keyType)
        RepositoryRegistry.register(this)
        createTable(table, connector, valueType)
    }


    private val fetcher: Fetcher<K, V> = SQLFetcher(table, connector, keyType, valueType)
    private val updater: Updater<K, V> = SQLUpdater(table, connector, keyType, valueType)
    private val inserter: Inserter<K, V> = SQLInserter(table, connector, keyType, valueType)
    private val deleter: Deleter<K, V> = SQLDeleter(table, connector, keyType, valueType)

    private val cache: Cache<K, V> =
            Caffeine.newBuilder()
                    .expireAfterAccess(evictionMinutes, TimeUnit.MINUTES)
                    .removalListener(
                            RemovalListener<K, V> { _, value, cause ->
                                if (value == null) return@RemovalListener;
                                if (cause == RemovalCause.EXPIRED)
                                    updater.update(value)
                            }
                    ).build()

    companion object {
        @JvmOverloads
        inline operator fun <reified K, reified V: Persistable<K>> invoke(connector: SQLConnector, table: String, eviction: Long = 30): SQLRepository<K, V>
        {
            return SQLRepository(K::class.java,V::class.java, connector, table, eviction)
        }
    }

    override fun with(key: K, operations: Consumer<V>) {
        this[key]?.let {
            operations.accept(it)
        } ?: fetcher.fetch(key) {
            cache[key] = it
            operations.accept(it)
        }
    }

    override fun withRelated(relations: MutableMap<String, Any>, operations: Consumer<V>?) {

    }

    override fun insert(value: V, onInsert: Function0<*>) {
        inserter.insert(value) { onInsert() }
    }

    override fun delete(key: K) {
        updater.notifyDelete(key)
        deleter.delete(key) {}
    }

    override fun update(value: V) {
        updater.update(value)
    }

    private operator fun get(k: K): V? {
        return cache.getIfPresent(k!!)
    }


    private fun createTable(table: String,connector: ISQLConnector, valueType: Class<V>) {
        val data = FieldFetcher.get(valueType) ?: throw IllegalArgumentException("Invalid table creation point...")
        connector.connectSync {
                val statement = it.prepareStatement("CREATE TABLE IF NOT EXISTS $table (${data.typedPair})")
                statement.executeUpdate()
        }
    }

    private val PersistentData.typedPair: String
                get() = fields.joinToString(
                        ",",
                        transform = {
                        val modifiers = "${if (it.isAnnotationPresent(NotNull::class.java)) "NOT NULL" else ""}"
                        "${it.persistantName} ${it.sqlType} $modifiers"
                    }
                ) + ",${key.persistantName} ${key.sqlType} PRIMARY KEY NOT NULL"


    fun invoke() {

    }
}
