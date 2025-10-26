package org.example.moneyexchanger.dao;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    void save(T entity);
    List<T> findAll();
    Optional<T> findById(Long id);
    void update(T entity);
    void delete(Long id);
}
