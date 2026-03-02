package org.example.library.repositories;

import org.example.library.contracts.Identifiable;
import org.example.library.contracts.Repository;

import java.util.*;

public class InMemoryRepository<T extends Identifiable<ID>, ID> implements Repository<T, ID> {

    protected final Map<ID, T> store = new HashMap<>();

    @Override
    public void save(T entity) {
        store.put(entity.getId(), entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(ID id) {
        store.remove(id);
    }

    // Phase 2.3
    public T findByIdOrDefault(ID id, T defaultValue) {
        return findById(id).orElse(defaultValue);
    }

    public <R> Optional<R> findAndTransform(ID id, java.util.function.Function<T, R> mapper) {
        return findById(id).map(mapper);
    }
}