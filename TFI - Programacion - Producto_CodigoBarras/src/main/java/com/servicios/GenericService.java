package com.servicios;

import java.util.List;

public interface GenericService<T> {
    
    /**
     * Inserta una nueva entidad en la base de datos
     * @param entity La entidad a insertar
     * @return La entidad insertada con ID generado, o null si falla
     */
    T insertar(T entity);
    
    /**
     * Actualiza una entidad existente en la base de datos
     * @param entity La entidad a actualizar
     * @return La entidad actualizada, o null si falla
     */
    T actualizar(T entity);
    
    /**
     * Elimina lógicamente una entidad por su ID
     * @param id El ID de la entidad a eliminar
     * @return La entidad eliminada, o null si no existe o falla
     */
    T eliminar(Long id);
    
    /**
     * Busca una entidad por su ID
     * @param id El ID de la entidad a buscar
     * @return La entidad encontrada, o null si no existe
     */
    T getById(Long id);
    
    /**
     * Obtiene todas las entidades no eliminadas
     * @return Lista de todas las entidades, lista vacía si no hay ninguna
     */
    List<T> getAll();
}
