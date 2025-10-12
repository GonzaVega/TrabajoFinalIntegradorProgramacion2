//ProductoService.java
package com.servicios;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.config.DatabaseConnection;
import com.dao.ProductoDaoImpl;
import com.entities.Producto;

public class ProductoService implements GenericService<Producto> {
    private ProductoDaoImpl productoDao;
    
    public ProductoService() {
        productoDao = new ProductoDaoImpl();
    }

    private boolean validarCamposObligatorios(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            System.out.println("❌ Error: El nombre del producto es obligatorio");
            return false;
        }
        
        if (producto.getMarca() == null || producto.getMarca().trim().isEmpty()) {
            System.out.println("❌ Error: La marca del producto es obligatoria");
            return false;
        }
        
        if (producto.getCategoria() == null || producto.getCategoria().trim().isEmpty()) {
            System.out.println("❌ Error: La categoría del producto es obligatoria");
            return false;
        }
        
        if (producto.getPrecio() <= 0) {
            System.out.println("❌ Error: El precio debe ser mayor a 0");
            return false;
        }
        
        if (producto.getPeso() != null && producto.getPeso() <= 0) {
            System.out.println("❌ Error: El peso debe ser mayor a 0 o null");
            return false;
        }
        
        return true;
    }

    private boolean validarReglasCodigoBarras(Producto producto) {
        if (producto.getCodigoBarras() != null) {
            Long codigoId = producto.getCodigoBarras().getId();
            Long productoId = producto.getId();
            
            if (esCodigoYaAsignado(codigoId, productoId)) {
                System.out.println("❌ Error: Este código de barras ya está asignado a otro producto");
                return false;
            }
        }
        
        return true;
    }

    @Override
    public Producto insertar(Producto producto) {
        if (!validarCamposObligatorios(producto)) {
            return null;
        }
        
        if (!validarReglasCodigoBarras(producto)) {
            return null;
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            Producto resultado = productoDao.crear(producto, conn);
            
            conn.commit();
            return resultado;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("❌ Error en rollback: " + rollbackEx.getMessage());
            }
            System.out.println("❌ Error al crear el producto: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.out.println("❌ Error al cerrar conexión: " + closeEx.getMessage());
            }
        }
    }
    
    @Override
    public Producto actualizar(Producto producto) {
        if (!validarCamposObligatorios(producto)) {
            return null;
        }
        
        if (!validarReglasCodigoBarras(producto)) {
            return null;
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            productoDao.actualizar(producto, conn);
            
            conn.commit();
            return producto;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("❌ Error en rollback: " + rollbackEx.getMessage());
            }
            System.out.println("❌ Error al actualizar el producto: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.out.println("❌ Error al cerrar conexión: " + closeEx.getMessage());
            }
        }
    }
    
    @Override
    public Producto eliminar(Long id) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            Producto productoExistente = productoDao.leer(id, conn);
            if (productoExistente == null) {
                conn.rollback();
                return null;
            }
            
            productoExistente.setEliminado(true);
            productoDao.actualizar(productoExistente, conn);
            
            conn.commit();
            return productoExistente;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("❌ Error en rollback: " + rollbackEx.getMessage());
            }
            System.out.println("❌ Error al eliminar el producto: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.out.println("❌ Error al cerrar conexión: " + closeEx.getMessage());
            }
        }
    }

    @Override
    public Producto getById(Long id) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            Producto producto = productoDao.leer(id, conn);
          
            if (producto != null && !producto.isEliminado()) {
                return producto;
            }
            return null;
        } catch (SQLException e) {
            System.out.println("❌ Error al buscar el producto: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException closeEx) {
                System.out.println("❌ Error al cerrar conexión: " + closeEx.getMessage());
            }
        }
    }

    @Override
    public List<Producto> getAll() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            List<Producto> productos = productoDao.leerTodos(conn);
            return productos.stream()
                    .filter(p -> !p.isEliminado())
                    .toList();
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener todos los productos: " + e.getMessage());
            e.printStackTrace();
            return List.of(); 
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException closeEx) {
                System.out.println("❌ Error al cerrar conexión: " + closeEx.getMessage());
            }
        }
    }

    // ============================================================================
    // MÉTODOS PENDIENTES DE IMPLEMENTACIÓN DAO - COMENTADOS
    // TODO: Descomentar cuando estén disponibles los métodos en ProductoDaoImpl
    // ============================================================================

    
    private boolean esCodigoYaAsignado(Long codigoBarrasId, Long productoId) {
        // TODO: Implementar cuando esté disponible buscarPorCodigoBarras() en ProductoDaoImpl
        /*
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            List<Producto> productosConCodigo = productoDao.buscarPorCodigoBarras(codigoBarrasId, conn);
            
            // SERVICE hace la validación de negocio
            return productosConCodigo.stream()
                    .anyMatch(p -> !p.getId().equals(productoId) && !p.isEliminado());
                    
        } catch (SQLException e) {
            System.out.println("❌ Error al validar código único: " + e.getMessage());
            return true; // En caso de error, asumir que ya existe (más seguro)
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException closeEx) {
                System.out.println("❌ Error al cerrar conexión: " + closeEx.getMessage());
            }
        }
        */
        return false; // Por ahora retorna false hasta tener método DAO
    }

    
    /*
    public List<Producto> buscarPorCategoria(String categoria) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            List<Producto> productos = productoDao.buscarPorCategoria(categoria, conn);
            // Filtrar productos no eliminados
            return productos.stream()
                    .filter(p -> !p.isEliminado())
                    .toList();
        } catch (SQLException e) {
            System.out.println("❌ Error al buscar productos por categoría: " + e.getMessage());
            return List.of();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException closeEx) {
                System.out.println("❌ Error al cerrar conexión: " + closeEx.getMessage());
            }
        }
    }
    */
}