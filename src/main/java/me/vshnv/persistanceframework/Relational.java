package me.vshnv.persistanceframework;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public interface Relational<V extends Persistable> {
    void withRelated(@NotNull Map<String, Object> relations, Consumer<V> operations);
}
