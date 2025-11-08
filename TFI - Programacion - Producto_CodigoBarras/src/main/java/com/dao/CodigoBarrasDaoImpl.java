//CodigoBarrasDaoImpl.java
package com.dao;

import com.entities.CodigoBarras;
import com.entities.TipoCodigoBarras;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del GenericDao para la entidad CodigoBarras. Contiene todas
 * las consultas SQL JDBC para interactuar con la tabla codigos_barras.
 */
public class CodigoBarrasDaoImpl implements GenericDao<CodigoBarras> {

    @Override
    public CodigoBarras crear(CodigoBarras codigoBarras, Connection connection) throws SQLException {
        String sql = "INSERT INTO codigos_barras (tipo, valor, fecha_asignacion, observaciones, eliminado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, codigoBarras.getTipo().name());
            ps.setString(2, codigoBarras.getValor());
            ps.setDate(3, Date.valueOf(codigoBarras.getFechaAsignacion()));
            ps.setString(4, codigoBarras.getObservaciones());
            ps.setBoolean(5, codigoBarras.isEliminado());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    codigoBarras.setId(rs.getLong(1));
                }
            }
        }
        return codigoBarras;
    }

    @Override
    public CodigoBarras leer(long id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM codigos_barras WHERE id = ? AND eliminado = false";
        CodigoBarras codigoBarras = null;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    codigoBarras = new CodigoBarras();
                    codigoBarras.setId(rs.getLong("id"));
                    codigoBarras.setTipo(TipoCodigoBarras.valueOf(rs.getString("tipo")));
                    codigoBarras.setValor(rs.getString("valor"));
                    codigoBarras.setFechaAsignacion(rs.getDate("fecha_asignacion").toLocalDate());
                    codigoBarras.setObservaciones(rs.getString("observaciones"));
                    codigoBarras.setEliminado(rs.getBoolean("eliminado"));
                }
            }
        }
        return codigoBarras;
    }

    @Override
    public List<CodigoBarras> leerTodos(Connection connection) throws SQLException {
        String sql = "SELECT * FROM codigos_barras WHERE eliminado = false";
        List<CodigoBarras> codigos = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CodigoBarras codigoBarras = new CodigoBarras();
                codigoBarras.setId(rs.getLong("id"));
                codigoBarras.setTipo(TipoCodigoBarras.valueOf(rs.getString("tipo")));
                codigoBarras.setValor(rs.getString("valor"));
                codigoBarras.setFechaAsignacion(rs.getDate("fecha_asignacion").toLocalDate());
                codigoBarras.setObservaciones(rs.getString("observaciones"));
                codigoBarras.setEliminado(rs.getBoolean("eliminado"));
                codigos.add(codigoBarras);
            }
        }
        return codigos;
    }

    @Override
    public void actualizar(CodigoBarras codigoBarras, Connection connection) throws SQLException {
        String sql = "UPDATE codigos_barras SET tipo = ?, valor = ?, fecha_asignacion = ?, observaciones = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, codigoBarras.getTipo().name());
            ps.setString(2, codigoBarras.getValor());
            ps.setDate(3, Date.valueOf(codigoBarras.getFechaAsignacion()));
            ps.setString(4, codigoBarras.getObservaciones());
            ps.setLong(5, codigoBarras.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(long id, Connection connection) throws SQLException {
        String sql = "UPDATE codigos_barras SET eliminado = true WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Busca códigos de barras activos que coincidan con un valor específico.
     *
     * @param valor El valor exacto del código de barras a buscar.
     * @param connection La conexión a la BD.
     * @return Una lista de códigos de barras que coinciden con el valor.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<CodigoBarras> buscarPorValor(String valor, Connection connection) throws SQLException {
        List<CodigoBarras> codigos = new ArrayList<>();
        String sql = "SELECT * FROM codigos_barras WHERE valor = ? AND eliminado = false";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CodigoBarras codigoBarras = new CodigoBarras();
                    codigoBarras.setId(rs.getLong("id"));
                    codigoBarras.setTipo(TipoCodigoBarras.valueOf(rs.getString("tipo")));
                    codigoBarras.setValor(rs.getString("valor"));
                    codigoBarras.setFechaAsignacion(rs.getDate("fecha_asignacion").toLocalDate());
                    codigoBarras.setObservaciones(rs.getString("observaciones"));
                    codigoBarras.setEliminado(rs.getBoolean("eliminado"));
                    codigos.add(codigoBarras);
                }
            }
        }
        return codigos;
    }

    /**
     * Filtra códigos de barras activos por su tipo (Enum).
     *
     * @param tipo El tipo (EAN13, EAN8, UPC) a buscar.
     * @param connection La conexión a la BD.
     * @return Una lista de códigos de barras que pertenecen a ese tipo.
     * @throws SQLException Si ocurre un error de SQL.
     */
    public List<CodigoBarras> buscarPorTipo(TipoCodigoBarras tipo, Connection connection) throws SQLException {
        List<CodigoBarras> codigos = new ArrayList<>();
        String sql = "SELECT * FROM codigos_barras WHERE tipo = ? AND eliminado = false";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tipo.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CodigoBarras codigoBarras = new CodigoBarras();
                    codigoBarras.setId(rs.getLong("id"));
                    codigoBarras.setTipo(TipoCodigoBarras.valueOf(rs.getString("tipo")));
                    codigoBarras.setValor(rs.getString("valor"));
                    codigoBarras.setFechaAsignacion(rs.getDate("fecha_asignacion").toLocalDate());
                    codigoBarras.setObservaciones(rs.getString("observaciones"));
                    codigoBarras.setEliminado(rs.getBoolean("eliminado"));
                    codigos.add(codigoBarras);
                }
            }
        }
        return codigos;
    }
}
