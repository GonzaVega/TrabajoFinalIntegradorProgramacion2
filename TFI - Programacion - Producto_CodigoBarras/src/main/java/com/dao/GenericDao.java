package com.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface GenericDao<T> {

    /**
     * 
     * @param entity 
     * @param connection 
     * @return 
     * @throws SQLException 
     * 
     */

    T crear(T entity, Connection connection) throws SQLException;

    /**
     * 
     * @param id 
     * @param connection 
     * @return 
     * @throws SQLException 
     * 
     */

    T leer(long id, Connection connection) throws SQLException;

    /**
     * 
     * @param connection 
     * @return 
     * @throws SQLException 
     * 
     */

    List<T> leerTodos(Connection connection) throws SQLException;

    /**
     * 
     * @param entity 
     * @param connection 
     * @throws SQLException 
     * 
     */
    void actualizar(T entity, Connection connection) throws SQLException;

    /**
     * 
     * @param id 
     * @param connection 
     * @throws SQLException 
     * 
     */
    
    void eliminar(long id, Connection connection) throws SQLException;
}