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
        String sql = "INSERT INTO codigos_barras (tipo, valor, fecha_asignacion, observaciones, eliminado) VALUES (?, ?, ?, ?, ?)";
        // Try-with-resources para asegurar que el PreparedStatement se cierre
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            System.out.println("DAO: 17 - Seteando parámetros..."); // TIPO: DAO 17
            ps.setString(1, codigoBarras.getTipo().name());
            ps.setString(2, codigoBarras.getValor());
            ps.setDate(3, Date.valueOf(codigoBarras.getFechaAsignacion()));
            ps.setString(4, codigoBarras.getObservaciones());
            ps.setBoolean(5, codigoBarras.isEliminado());

            System.out.println("DAO: 18 - Ejecutando UPDATE..."); // TIPO: DAO 18
            ps.executeUpdate();
            System.out.println("DAO: 19 - UPDATE ejecutado, sin errores SQL."); // TIPO: DAO 19

            // Obtener el ID generado por la base de datos
            try (ResultSet rs = ps.getGeneratedKeys()) {
                System.out.println("DAO: 20 - Obteniendo ID generado..."); // TIPO: DAO 20
                if (rs.next()) {
                    codigoBarras.setId(rs.getLong(1));
                    System.out.println("DAO: 21 - ID asignado."); // TIPO: DAO 21
                }
            }
        }
        return codigoBarras;
    }


    /* @Override
    public CodigoBarras crear(CodigoBarras codigoBarras, Connection connection) throws SQLException {
        System.out.println("16");
        String sql = "INSERT INTO codigos_barras (tipo, valor, fecha_asignacion, observaciones, eliminado) VALUES (?, ?, ?, ?, ?)";
        // Try-with-resources para asegurar que el PreparedStatement se cierre
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
           System.out.println("17");
            ps.setString(1, codigoBarras.getTipo().name());
            ps.setString(2, codigoBarras.getValor());
            ps.setDate(3, Date.valueOf(codigoBarras.getFechaAsignacion()));
            ps.setString(4, codigoBarras.getObservaciones());
            ps.setBoolean(5, codigoBarras.isEliminado());

            System.out.println("18");
            ps.executeUpdate();

            // Obtener el ID generado por la base de datos
            System.out.println("19");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                System.out.println("20");
                if (rs.next()) {
                    System.out.println("21");
                    codigoBarras.setId(rs.getLong(1));
                }
            }
        }
        return codigoBarras;
    } */

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
     * @return Una lista de códigos de barras (usualmente uno) que coinciden con
     * el valor.
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
     * Filtra códigos de barras activos por su tipo.
     *
     * @param tipo El tipo (EAN13, EAN8, UPC) a buscar.
     * @param connection La conexión a la BD.
     * @return Una lista de códigos de barras que pertenecen a ese tipo.
     */
    public List<CodigoBarras> buscarPorTipo(TipoCodigoBarras tipo, Connection connection) throws SQLException {
        List<CodigoBarras> codigos = new ArrayList<>();
        String sql = "SELECT * FROM codigos_barras WHERE tipo = ? AND eliminado = false";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tipo.name()); // Convertimos el Enum a String
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
