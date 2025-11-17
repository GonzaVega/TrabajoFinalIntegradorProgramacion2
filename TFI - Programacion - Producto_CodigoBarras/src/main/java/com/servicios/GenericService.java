package com.servicios;

import java.util.List;

public interface GenericService<T> {
    
    /**
     * 
     * @param entity 
     * @return 
     * 
     */
    T insertar(T entity);
    
    /**
     * 
     * @param entity 
     * @return 
     * 
     */
    T actualizar(T entity);
    
    /**
     * 
     * @param id 
     * @return 
     * 
     */
    T eliminar(Long id);
    
    /**
     * 
     * @param id 
     * @return 
     * 
     */
    T getById(Long id);
    
    /**
     * 
     * @return 
     * 
     */
    List<T> getAll();
}
