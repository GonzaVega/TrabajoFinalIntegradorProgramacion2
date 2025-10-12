//CodigoBarrasService.java
package com.servicios;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.config.DatabaseConnection;
import com.dao.CodigoBarrasDaoImpl;
import com.entities.CodigoBarras;
import com.entities.TipoCodigoBarras;

public class CodigoBarrasService implements GenericService<CodigoBarras> {
    private CodigoBarrasDaoImpl codigoBarrasDao;
    
    public CodigoBarrasService() {
        codigoBarrasDao = new CodigoBarrasDaoImpl();
    }

    private boolean validarCamposObligatorios(CodigoBarras codigoBarras) {
        if (codigoBarras.getValor() == null || codigoBarras.getValor().trim().isEmpty()) {
            System.out.println("❌ Error: El valor del código de barras es obligatorio");
            return false;
        }
        
        if (codigoBarras.getTipo() == null) {
            System.out.println("❌ Error: El tipo del código de barras es obligatorio");
            return false;
        }
        
        return true;
    }

    private boolean validarReglasNegocio(CodigoBarras codigoBarras) {
        if (!validarFormatoSegunTipo(codigoBarras)) {
            return false;
        }
        
        if (esValorDuplicado(codigoBarras.getValor(), codigoBarras.getId())) {
            System.out.println("❌ Error: Ya existe un código de barras con este valor");
            return false;
        }
        
        return true;
    }

    private boolean validarFormatoSegunTipo(CodigoBarras codigoBarras) {
        String valor = codigoBarras.getValor().trim();
        TipoCodigoBarras tipo = codigoBarras.getTipo();
        
        switch (tipo) {
            case EAN13:
                if (!valor.matches("\\d{13}")) {
                    System.out.println("❌ Error: EAN-13 debe tener exactamente 13 dígitos");
                    return false;
                }
                break;
                
            case EAN8:
                if (!valor.matches("\\d{8}")) {
                    System.out.println("❌ Error: EAN-8 debe tener exactamente 8 dígitos");
                    return false;
                }
                break;
                
            case UPC:
                if (!valor.matches("\\d{12}")) {
                    System.out.println("❌ Error: UPC debe tener exactamente 12 dígitos");
                    return false;
                }
                break;
                
            default:
                if (valor.isEmpty()) {
                    System.out.println("❌ Error: El valor del código no puede estar vacío");
                    return false;
                }
                break;
        }
        return true;
    }

    @Override
    public CodigoBarras insertar(CodigoBarras codigoBarras) {
        if (!validarCamposObligatorios(codigoBarras)) {
            return null;
        }
        
        if (!validarReglasNegocio(codigoBarras)) {
            return null;
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            CodigoBarras resultado = codigoBarrasDao.crear(codigoBarras, conn);
            
            conn.commit();
            return resultado;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("❌ Error en rollback: " + rollbackEx.getMessage());
            }
            System.out.println("❌ Error al crear el código de barras: " + e.getMessage());
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
    public CodigoBarras actualizar(CodigoBarras codigoBarras) {
        if (!validarCamposObligatorios(codigoBarras)) {
            return null;
        }
        
        if (!validarReglasNegocio(codigoBarras)) {
            return null;
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            codigoBarrasDao.actualizar(codigoBarras, conn);
            
            conn.commit();
            return codigoBarras;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("❌ Error en rollback: " + rollbackEx.getMessage());
            }
            System.out.println("❌ Error al editar el código de barras: " + e.getMessage());
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
    public CodigoBarras eliminar(Long id) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            CodigoBarras codigoExistente = codigoBarrasDao.leer(id, conn);
            if (codigoExistente == null) {
                conn.rollback();
                return null;
            }
            
            codigoBarrasDao.eliminar(id, conn);
            
            conn.commit();
            return codigoExistente;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("❌ Error en rollback: " + rollbackEx.getMessage());
            }
            System.out.println("❌ Error al eliminar el código de barras: " + e.getMessage());
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
    public CodigoBarras getById(Long id) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            return codigoBarrasDao.leer(id, conn);
        } catch (SQLException e) {
            System.out.println("❌ Error al buscar el código de barras: " + e.getMessage());
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
    public List<CodigoBarras> getAll() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            return codigoBarrasDao.leerTodos(conn);
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener todos los códigos de barras: " + e.getMessage());
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
    // TODO: Descomentar cuando estén disponibles los métodos en CodigoBarrasDaoImpl
    // ============================================================================

    private boolean esValorDuplicado(String valor, Long codigoBarrasId) {
        // TODO: Implementar cuando esté disponible buscarPorValor() en CodigoBarrasDaoImpl
        /*
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            List<CodigoBarras> codigosConValor = codigoBarrasDao.buscarPorValor(valor, conn);
            
            // SERVICE hace la validación de negocio
            return codigosConValor.stream()
                    .anyMatch(c -> !c.getId().equals(codigoBarrasId));
                    
        } catch (SQLException e) {
            System.out.println("❌ Error al validar valor único: " + e.getMessage());
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
    public List<CodigoBarras> buscarPorTipo(TipoCodigoBarras tipo) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            return codigoBarrasDao.buscarPorTipo(tipo, conn);
        } catch (SQLException e) {
            System.out.println("❌ Error al buscar códigos por tipo: " + e.getMessage());
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