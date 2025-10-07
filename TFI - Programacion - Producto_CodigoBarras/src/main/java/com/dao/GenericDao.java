//GenericDao.java
package com.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface GenericDao<T> {

    /**
     * Inserta una nueva entidad en la base de datos.
     * @param entity La entidad a crear.
     * @param connection La conexión a la BD para la transacción.
     * @return La entidad creada, usualmente con el ID asignado por la BD.
     * @throws SQLException Si ocurre un error de SQL.
     */
    T crear(T entity, Connection connection) throws SQLException;

    /**
     * Busca una entidad por su ID.
     * @param id El ID de la entidad a buscar.
     * @param connection La conexión a la BD.
     * @return La entidad encontrada, o null si no existe.
     * @throws SQLException Si ocurre un error de SQL.
     */
    T leer(long id, Connection connection) throws SQLException;

    /**
     * Devuelve una lista de todas las entidades (no eliminadas lógicamente).
     * @param connection La conexión a la BD.
     * @return Una lista de entidades.
     * @throws SQLException Si ocurre un error de SQL.
     */
    List<T> leerTodos(Connection connection) throws SQLException;

    /**
     * Actualiza una entidad existente en la base de datos.
     * @param entity La entidad con los datos actualizados.
     * @param connection La conexión a la BD para la transacción.
     * @throws SQLException Si ocurre un error de SQL.
     */
    void actualizar(T entity, Connection connection) throws SQLException;

    /**
     * Realiza una baja lógica de una entidad por su ID.
     * @param id El ID de la entidad a eliminar.
     * @param connection La conexión a la BD para la transacción.
     * @throws SQLException Si ocurre un error de SQL.
     */
    void eliminar(long id, Connection connection) throws SQLException;
}