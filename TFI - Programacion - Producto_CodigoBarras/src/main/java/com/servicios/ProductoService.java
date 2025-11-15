package com.servicios;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.config.DatabaseConnection;
import com.dao.ProductoDaoImpl;
import com.entities.Producto;
import com.utils.ManejadorExcepciones;

public class ProductoService implements GenericService<Producto> {
	private ProductoDaoImpl productoDao;

	public ProductoService() {
		productoDao = new ProductoDaoImpl();
	}

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

	private boolean validarReglasCodigoBarras(Producto producto) {
		if (producto.getCodigoBarras() != null) {
			Long codigoId = producto.getCodigoBarras().getId();
			Long productoId = producto.getId();

			if (esCodigoYaAsignado(codigoId, productoId)) {
				ManejadorExcepciones.manejarErrorValidacion("Código de barras", "Ya está asignado a otro producto");
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
   
	@Override
	public Producto eliminar(Long id) {
		Connection conn = null;
		try {
			conn = DatabaseConnection.getConnection();
			conn.setAutoCommit(false);

			Producto productoExistente = productoDao.leer(id, conn);
			if (productoExistente == null) {
				conn.rollback();
				System.out.println(" No existe producto con ID: " + id);
				return null;
			}

			productoExistente.setEliminado(true);
			productoDao.eliminar(id, conn);

			conn.commit();
			return productoExistente;
		} catch (SQLException e) {
			try {
				if (conn != null) conn.rollback();
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
			ManejadorExcepciones.manejarErrorBaseDatos(e, "Buscar producto por ID");
			return null;
		} finally {
			try {
				if (conn != null) conn.close();
			} catch (SQLException closeEx) {
				ManejadorExcepciones.manejarErrorBaseDatos(closeEx, "Cerrar conexión");
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
			ManejadorExcepciones.manejarErrorBaseDatos(e, "Obtener todos los productos");
			return List.of();
		} finally {
			try {
				if (conn != null) conn.close();
			} catch (SQLException closeEx) {
				ManejadorExcepciones.manejarErrorBaseDatos(closeEx, "Cerrar conexión");
			}
		}
	}

	private boolean esCodigoYaAsignado(Long codigoBarrasId, Long productoId) {
		Connection conn = null;
		try {
			conn = DatabaseConnection.getConnection();

			List<Producto> productosConCodigo = productoDao.buscarPorCodigoBarras(codigoBarrasId, conn);

			return productosConCodigo.stream()
					.anyMatch(p -> !p.getId().equals(productoId) && !p.isEliminado());

		} catch (SQLException e) {
			ManejadorExcepciones.manejarErrorBaseDatos(e, "Validar unicidad de código de barras");
			return true;
		} finally {
			try {
				if (conn != null) conn.close();
			} catch (SQLException closeEx) {
				ManejadorExcepciones.manejarErrorBaseDatos(closeEx, "Cerrar conexión");
			}
		}
	}

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
				if (conn != null) conn.close();
			} catch (SQLException closeEx) {
				ManejadorExcepciones.manejarErrorBaseDatos(closeEx, "Cerrar conexión");
			}
		}
	}
}