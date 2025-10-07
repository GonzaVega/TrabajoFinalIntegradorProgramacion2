//CodigoBarrasDaoImpl.java
package com.dao;

import com.entities.CodigoBarras;
import com.entities.TipoCodigoBarras;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CodigoBarrasDaoImpl implements GenericDao<CodigoBarras> {

    @Override
    public CodigoBarras crear(CodigoBarras codigoBarras, Connection connection) throws SQLException {
        String sql = "INSERT INTO codigo_barras (tipo, valor, fecha_asignacion, observaciones, eliminado) VALUES (?, ?, ?, ?, ?)";
        // Usamos try-with-resources para asegurar que el PreparedStatement se cierre
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, codigoBarras.getTipo().name());
            ps.setString(2, codigoBarras.getValor());
            ps.setDate(3, Date.valueOf(codigoBarras.getFechaAsignacion()));
            ps.setString(4, codigoBarras.getObservaciones());
            ps.setBoolean(5, codigoBarras.isEliminado());
            
            ps.executeUpdate();

            // Obtenemos el ID generado por la base de datos
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
        String sql = "SELECT * FROM codigo_barras WHERE id = ? AND eliminado = false";
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
        String sql = "SELECT * FROM codigo_barras WHERE eliminado = false";
        List<CodigoBarras> codigos = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
        String sql = "UPDATE codigo_barras SET tipo = ?, valor = ?, fecha_asignacion = ?, observaciones = ? WHERE id = ?";
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
        String sql = "UPDATE codigo_barras SET eliminado = true WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}