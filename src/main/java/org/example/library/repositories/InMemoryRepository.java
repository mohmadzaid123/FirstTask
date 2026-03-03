package org.example.library.repositories;

import org.example.library.annotations.Audited;
import org.example.library.contracts.Identifiable;
import org.example.library.contracts.Repository;

import java.util.*;

public class InMemoryRepository<T extends Identifiable<ID>, ID> implements Repository<T, ID> {

    protected final Map<ID, T> store = new HashMap<>();

    @Override
    @Audited(action = "SAVE")
    public synchronized void save(T entity) {
        if (entity == null) throw new IllegalArgumentException("entity is null");
        store.put(entity.getId(), entity);
    }
    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
        synchronized (store) {
            return new ArrayList<>(store.values());
        }
    }

    @Override
    @Audited(action = "DELETE")
    public synchronized void delete(ID id) {
        if (id == null) return;
        store.remove(id);
    }


    public T findByIdOrDefault(ID id, T defaultValue) {
        return findById(id).orElse(defaultValue);
    }

    public <R> Optional<R> findAndTransform(ID id, java.util.function.Function<T, R> mapper) {
        return findById(id).map(mapper);
    }
}