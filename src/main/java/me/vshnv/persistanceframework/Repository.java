package me.vshnv.persistanceframework;

import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface Repository<K, V extends Persistable<K>> {
    void with( @NotNull K key, @NotNull Consumer<V> operations);
    void insert(@NotNull V value,@NotNull Function0 onInsert);
    void delete( @NotNull K key);
    void update( @NotNull V value);

}
