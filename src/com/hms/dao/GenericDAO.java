package com.hms.dao;

import com.hms.exception.HospitalException;
import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface using Generics.
 * Demonstrates: Generics (GenericDAO<T, ID>), Design Pattern (DAO).
 *
 * @param <T>  Entity type
 * @param <ID> Primary key type
 */
public interface GenericDAO<T, ID> {

    /**
     * Insert a new entity.
     * @return generated ID
     */
    ID insert(T entity) throws HospitalException;

    /**
     * Update an existing entity.
     */
    void update(T entity) throws HospitalException;

    /**
     * Delete by primary key.
     */
    void delete(ID id) throws HospitalException;

    /**
     * Find by primary key.
     */
    Optional<T> findById(ID id) throws HospitalException;

    /**
     * Retrieve all entities.
     */
    List<T> findAll() throws HospitalException;
}
