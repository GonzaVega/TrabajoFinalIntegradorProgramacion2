// ProductoService.java
package com.servicios;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.config.DatabaseConnection;
import com.dao.ProductoDaoImpl;
import com.dao.CodigoBarrasDaoImpl; // Importado para transacciones atómicas
import com.entities.CodigoBarras; // Importado para transacciones atómicas
import com.entities.Producto;
import com.utils.ManejadorExcepciones;

/**
 * Capa de servicio para la entidad Producto. Maneja la lógica de negocio,
 * validaciones y transacciones.
 */
public class ProductoService implements GenericService<Producto> {

    private ProductoDaoImpl productoDao;
    private CodigoBarrasDaoImpl codigoBarrasDao; // Para operaciones atómicas

    public ProductoService() {
        productoDao = new ProductoDaoImpl();
        codigoBarrasDao = new CodigoBarrasDaoImpl();
    }

    /**
     * Valida que los campos obligatorios del producto no estén vacíos.
     *
     * @param producto El producto a validar.
     * @return true si es válido, false en caso contrario.
     */
    private boolean validarCamposObligatorios(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            ManejadorExcepciones.manejarErrorValidacion("Nombre del producto", "Campo obligatorio");
            return false;
        }
        if (producto.getMarca() == null || producto.getMarca().trim().isEmpty()) {
            ManejadorExcepciones.manejarErrorValidacion("Marca del producto", "Campo obligatorio");
            return false;
        }
        if (producto.getCategoria() == null || producto.getCategoria().trim().isEmpty()) {
            ManejadorExcepciones.manejarErrorValidacion("Categoría del producto", "Campo obligatorio");
            return false;
        }
        if (producto.getPrecio() <= 0) {
            ManejadorExcepciones.manejarErrorValidacion("Precio", "Debe ser mayor a 0");
            return false;
        }
        if (producto.getPeso() != null && producto.getPeso() < 0) {
            ManejadorExcepciones.manejarErrorValidacion("Peso", "El peso debe ser positivo.");
            return false;
        }
        return true;
    }

    /**
     * Valida las reglas de negocio de la relación 1-a-1. Se ejecuta dentro de
     * una transacción existente.
     *
     * @param producto El producto a validar.
     * @param conn La conexión SQL existente.
     * @return true si es válido, false en caso contrario.
     */
    private boolean validarReglasCodigoBarras(Producto producto, Connection conn) {
        if (producto.getCodigoBarras() != null) {
            Long codigoId = producto.getCodigoBarras().getId();
            Long productoId = producto.getId();

            if (esCodigoYaAsignado(codigoId, productoId, conn)) {
                ManejadorExcepciones.manejarErrorValidacion("Código de barras", "Ya está asignado a otro producto");
                return false;
            }
        }
        return true;
    }

    /**
     * Inserta un nuevo producto. Si el producto incluye un CodigoBarras nuevo
     * (sin ID), lo crea también en la misma transacción atómica.
     *
     * @param producto El producto a insertar (puede incluir un código de barras
     * nuevo).
     * @return El producto insertado con su ID, o null si falla.
     */
    @Override
    public Producto insertar(Producto producto) {
        if (!validarCamposObligatorios(producto)) {
            return null;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // INICIO DE LA TRANSACCIÓN ÚNICA

            // Lógica Atómica:
            if (producto.getCodigoBarras() != null && producto.getCodigoBarras().getId() == null) {
                // 1. Validar unicidad del valor del código nuevo
                if (esCodigoValorDuplicado(producto.getCodigoBarras().getValor(), null, conn)) {
                    ManejadorExcepciones.manejarErrorValidacion("Código de barras", "El valor ya existe (detectado en transacción)");
                    conn.rollback();
                    return null;
                }
                // 2. Crear el código de barras
                CodigoBarras codigoCreado = codigoBarrasDao.crear(producto.getCodigoBarras(), conn);
                producto.setCodigoBarras(codigoCreado);
            }

            // 3. Validar reglas 1-a-1 (que el código no esté ya asignado)
            if (!validarReglasCodigoBarras(producto, conn)) {
                conn.rollback();
                return null;
            }

            // 4. Crear el producto
            Producto resultado = productoDao.crear(producto, conn);

            conn.commit(); // COMMIT DE TODAS LAS OPERACIONES
            return resultado;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                ManejadorExcepciones.manejarErrorBaseDatos(rollbackEx, "Rollback al crear producto");
            }
            ManejadorExcepciones.manejarErrorBaseDatos(e, "Crear producto");
            return null;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                ManejadorExcepciones.manejarErrorBaseDatos(closeEx, "Cerrar conexión");
            }
        }
    }

    /**
     * Actualiza un producto existente en una transacción.
     *
     * @param producto El producto con los datos a actualizar.
     * @return El producto actualizado, o null si falla.
     */
    @Override

    public Producto actualizar(Producto producto) {
        if (!validarCamposObligatorios(producto)) {
            return null;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Validar reglas de negocio dentro de la transacción
            if (!validarReglasCodigoBarras(producto, conn)) {
                conn.rollback();
                return null;
            }

            productoDao.actualizar(producto, conn);

            conn.commit();
            return producto;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                ManejadorExcepciones.manejarErrorBaseDatos(rollbackEx, "Rollback al actualizar producto");
            }
            ManejadorExcepciones.manejarErrorBaseDatos(e, "Actualizar producto");
            return null;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                ManejadorExcepciones.manejarErrorBaseDatos(closeEx, "Cerrar conexión");
            }
        }
    }

    /**
     * Realiza una baja lógica de un producto.
     *
     * @param id El ID del producto a eliminar.
     * @return El producto que fue eliminado lógicamente, o null si no se
     * encontró.
     */
    @Override
    public Producto eliminar(Long id) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Producto productoExistente = productoDao.leer(id, conn);
            if (productoExistente == null) {
                conn.rollback();
                System.out.println("❌ No existe producto con ID: " + id);
                return null;
            }

            productoDao.eliminar(id, conn); // Llama al DAO que hace "SET eliminado = true"

            conn.commit();
            productoExistente.setEliminado(true); // Sincroniza el objeto en Java
            return productoExistente;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                ManejadorExcepciones.manejarErrorBaseDatos(rollbackEx, "Rollback al eliminar producto");
            }
            ManejadorExcepciones.manejarErrorBaseDatos(e, "Eliminar producto (marcado lógico)");
            return null;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                ManejadorExcepciones.manejarErrorBaseDatos(closeEx, "Cerrar conexión");
            }
        }
    }

    /**
     * Obtiene un producto por su ID.
     *
     * @param id El ID del producto.
     * @return El producto encontrado, o null si no existe o fue eliminado.
     */
    @Override
    public Producto getById(Long id) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            Producto producto = productoDao.leer(id, conn);

            if (producto != null && !producto.isEliminado()) {
                return producto;
            }
            return null; // No se encontró o está eliminado
        } catch (SQLException e) {
            ManejadorExcepciones.manejarErrorBaseDatos(e, "Buscar producto por ID");
            return null;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException closeEx) {
                ManejadorExcepciones.manejarErrorBaseDatos(closeEx, "Cerrar conexión");
            }
        }
    }

    /**
     * Obtiene una lista de todos los productos no eliminados.
     *
     * @return Una lista de productos.
     */
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
            ManejadorExcepciones.manejarErrorBaseDatos(e, "Obtener todos los productos");
            return List.of();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException closeEx) {
                ManejadorExcepciones.manejarErrorBaseDatos(closeEx, "Cerrar conexión");
            }
        }
    }

    /**
     * Verifica si un código de barras ya está asignado a OTRO producto. Se usa
     * dentro de una transacción.
     */
    private boolean esCodigoYaAsignado(Long codigoBarrasId, Long productoId, Connection conn) {
        if (codigoBarrasId == null) {
            return false; // Si no hay código, no puede estar asignado
        }
        try {
            List<Producto> productosConCodigo = productoDao.buscarPorCodigoBarras(codigoBarrasId, conn);
            return productosConCodigo.stream()
                    .anyMatch(p -> !p.getId().equals(productoId) && !p.isEliminado());
        } catch (SQLException e) {
            ManejadorExcepciones.manejarErrorBaseDatos(e, "Validar unicidad de código de barras");
            return true; // Asumir que existe en caso de error
        }
    }

    /**
     * Verifica si el valor de un código de barras ya existe en OTRO código. Se
     * usa dentro de una transacción.
     */
    private boolean esCodigoValorDuplicado(String valor, Long codigoBarrasId, Connection conn) {
        if (valor == null) {
            return false;
        }
        try {
            List<CodigoBarras> codigosConValor = codigoBarrasDao.buscarPorValor(valor, conn);
            return codigosConValor.stream()
                    .anyMatch(c -> !c.getId().equals(codigoBarrasId));
        } catch (SQLException e) {
            ManejadorExcepciones.manejarErrorBaseDatos(e, "Validar unicidad de valor de código de barras");
            return true;
        }
    }

    /**
     * Busca productos por categoría.
     *
     * @param categoria La categoría a filtrar.
     * @return Una lista de productos que coinciden.
     */
    public List<Producto> buscarPorCategoria(String categoria) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            List<Producto> productos = productoDao.buscarPorCategoria(categoria, conn);
            return productos.stream()
                    .filter(p -> !p.isEliminado())
                    .toList();
        } catch (SQLException e) {
            ManejadorExcepciones.manejarErrorBaseDatos(e, "Buscar productos por categoría");
            return List.of();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException closeEx) {
                ManejadorExcepciones.manejarErrorBaseDatos(closeEx, "Cerrar conexión");
            }
        }
    }
}
