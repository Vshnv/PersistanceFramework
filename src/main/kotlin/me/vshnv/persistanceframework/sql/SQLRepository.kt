package me.vshnv.persistanceframework.sql

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.RemovalCause
import com.github.benmanes.caffeine.cache.RemovalListener
import me.vshnv.persistanceframework.*
import me.vshnv.persistanceframework.field.FieldFetcher
import me.vshnv.persistanceframework.field.RepositoryRegistry
import me.vshnv.persistanceframework.sql.connector.ISQLConnector
import me.vshnv.persistanceframework.sql.connector.SQLConnector
import org.checkerframework.checker.nullness.qual.Nullable
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.Function
import kotlin.coroutines.CoroutineContext
class SQLRepository<K, V : Persistable<K>>
    @JvmOverloads constructor (
        keyType: Class<K>,
        valueType: Class<V>,
        connector: ISQLConnector,
        table: String, evictionMinutes: Long = 20): Repository<K, V>, Relational<V>{

    init {
        FieldFetcher.registerClass(valueType, keyType)
        RepositoryRegistry.register(this)
    }


    private val fetcher: Fetcher<K, V> = SQLFetcher(table, connector, keyType, valueType)
    private val updater: Updater<K, V> = SQLUpdater(table, connector, keyType, valueType)
    private val inserter: Inserter<K, V> = SQLInserter(table, connector, keyType, valueType)
    private val deleter: Deleter<K, V> = SQLDeleter(table, connector, keyType, valueType)

    private val cache: Cache<K, V> =
            Caffeine.newBuilder()
                    .expireAfterAccess(evictionMinutes, TimeUnit.MINUTES)
                    .removalListener(
                            RemovalListener<K,V>{ _, value, cause ->
                                if ( value == null ) return@RemovalListener;
                                if ( cause == RemovalCause.EXPIRED )
                                    updater.update(value)
                            }
                    ).build()




    override fun with(key: K, operations: Consumer<V>) {
        this[key]?.let {
            operations.accept(it)
        } ?: fetcher.fetch(key) {
            if (it == null)return@fetch TODO("Remove nullability from fetch")
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

}
