package com.servicios;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.config.DatabaseConnection;
import com.dao.CodigoBarrasDaoImpl;
import com.entities.CodigoBarras;
import com.entities.TipoCodigoBarras;
import com.utils.ManejadorExcepciones;

public class CodigoBarrasService implements GenericService<CodigoBarras> {
	private CodigoBarrasDaoImpl codigoBarrasDao;

	public CodigoBarrasService() {
		codigoBarrasDao = new CodigoBarrasDaoImpl();
	}

	private boolean validarCamposObligatorios(CodigoBarras codigoBarras) {
		if (codigoBarras.getValor() == null || codigoBarras.getValor().trim().isEmpty()) {
			ManejadorExcepciones.manejarErrorValidacion("Valor del código de barras", "Campo obligatorio");
			return false;
		}

		if (codigoBarras.getTipo() == null) {
			ManejadorExcepciones.manejarErrorValidacion("Tipo del código de barras", "Campo obligatorio");
			return false;
		}

		return true;
	}

	private boolean validarReglasNegocio(CodigoBarras codigoBarras) {
		if (!validarFormatoSegunTipo(codigoBarras)) {
			return false;
		}

		if (esValorDuplicado(codigoBarras.getValor(), codigoBarras.getId())) {
			ManejadorExcepciones.manejarErrorValidacion("Valor del código de barras", "Ya existe un código con este valor");
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
					ManejadorExcepciones.manejarErrorValidacion("Formato EAN-13", "Debe tener exactamente 13 dígitos");
					return false;
				}
				break;

			case EAN8:
				if (!valor.matches("\\d{8}")) {
					ManejadorExcepciones.manejarErrorValidacion("Formato EAN-8", "Debe tener exactamente 8 dígitos");
					return false;
				}
				break;

			case UPC:
				if (!valor.matches("\\d{12}")) {
					ManejadorExcepciones.manejarErrorValidacion("Formato UPC", "Debe tener exactamente 12 dígitos");
					return false;
				}
				break;

			default:
				if (valor.isEmpty()) {
					ManejadorExcepciones.manejarErrorValidacion("Valor del código", "No puede estar vacío");
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
            System.out.println("TIPO: A4 - Transacción iniciada."); // TIPO: A4

			CodigoBarras resultado = codigoBarrasDao.crear(codigoBarras, conn);

			conn.commit();
			return resultado;
            
		} catch (SQLException e) {
			try {
				if (conn != null) conn.rollback();
			} catch (SQLException rollbackEx) {
				ManejadorExcepciones.manejarErrorBaseDatos(rollbackEx, "Rollback al crear código de barras");
			}
			ManejadorExcepciones.manejarErrorBaseDatos(e, "Crear código de barras");
			return null;
            
        } catch (RuntimeException e) { 
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                ManejadorExcepciones.manejarErrorBaseDatos(rollbackEx, "Rollback tras error de lógica/runtime");
            }
            e.printStackTrace();
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
				ManejadorExcepciones.manejarErrorBaseDatos(rollbackEx, "Rollback al actualizar código de barras");
			}
			ManejadorExcepciones.manejarErrorBaseDatos(e, "Actualizar código de barras");
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
	public CodigoBarras eliminar(Long id) {
		Connection conn = null;
		try {
			conn = DatabaseConnection.getConnection();
			conn.setAutoCommit(false);

			CodigoBarras codigoExistente = codigoBarrasDao.leer(id, conn);
			if (codigoExistente == null) {
				conn.rollback();
				System.out.println("❌ No existe código de barras con ID: " + id);
				return null;
			}

			codigoBarrasDao.eliminar(id, conn);

			conn.commit();
			return codigoExistente;
		} catch (SQLException e) {
			try {
				if (conn != null) conn.rollback();
			} catch (SQLException rollbackEx) {
				ManejadorExcepciones.manejarErrorBaseDatos(rollbackEx, "Rollback al eliminar código de barras");
			}
			ManejadorExcepciones.manejarErrorBaseDatos(e, "Eliminar código de barras");
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
	public CodigoBarras getById(Long id) {
		Connection conn = null;
		try {
			conn = DatabaseConnection.getConnection();

			return codigoBarrasDao.leer(id, conn);
		} catch (SQLException e) {
			ManejadorExcepciones.manejarErrorBaseDatos(e, "Buscar código de barras por ID");
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
	public List<CodigoBarras> getAll() {
		Connection conn = null;
		try {
			conn = DatabaseConnection.getConnection();

			return codigoBarrasDao.leerTodos(conn);
		} catch (SQLException e) {
			ManejadorExcepciones.manejarErrorBaseDatos(e, "Obtener todos los códigos de barras");
			return List.of();
		} finally {
			try {
				if (conn != null) conn.close();
			} catch (SQLException closeEx) {
				ManejadorExcepciones.manejarErrorBaseDatos(closeEx, "Cerrar conexión");
			}
		}
	}


	private boolean esValorDuplicado(String valor, Long codigoBarrasId) {
		Connection conn = null;
		try {
			conn = DatabaseConnection.getConnection();

			List<CodigoBarras> codigosConValor = codigoBarrasDao.buscarPorValor(valor, conn);
            
            if (codigoBarrasId == null) {
                return !codigosConValor.isEmpty(); 
            }

			return codigosConValor.stream()
					.anyMatch(c -> !c.getId().equals(codigoBarrasId));

		} catch (SQLException e) {
			ManejadorExcepciones.manejarErrorBaseDatos(e, "Validar unicidad de valor de código de barras");
			return true; 
		} finally {
			try {
				if (conn != null) conn.close();
			} catch (SQLException closeEx) {
				ManejadorExcepciones.manejarErrorBaseDatos(closeEx, "Cerrar conexión");
			}
		}
	}


	public List<CodigoBarras> buscarPorTipo(TipoCodigoBarras tipo) {
		Connection conn = null;
		try {
			conn = DatabaseConnection.getConnection();

			return codigoBarrasDao.buscarPorTipo(tipo, conn);
		} catch (SQLException e) {
			ManejadorExcepciones.manejarErrorBaseDatos(e, "Buscar códigos de barras por tipo");
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