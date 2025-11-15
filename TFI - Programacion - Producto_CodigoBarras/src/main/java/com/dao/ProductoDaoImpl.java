package com.dao;

import com.entities.CodigoBarras;
import com.entities.Producto;
import com.entities.TipoCodigoBarras;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDaoImpl implements GenericDao<Producto> {

    @Override
    public Producto crear(Producto producto, Connection connection) throws SQLException {
        String sql = "INSERT INTO productos (nombre, marca, categoria, precio, peso, eliminado, codigos_barras_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getMarca());
            ps.setString(3, producto.getCategoria());
            ps.setDouble(4, producto.getPrecio());
            if (producto.getPeso() != null) {
                ps.setDouble(5, producto.getPeso());
            } else {
                ps.setNull(5, Types.DOUBLE);
            }
            ps.setBoolean(6, producto.isEliminado());

            if (producto.getCodigoBarras() != null && producto.getCodigoBarras().getId() != null) {
                ps.setLong(7, producto.getCodigoBarras().getId());
            } else {
                ps.setNull(7, Types.BIGINT); // Si no hay código, insertamos NULL
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    producto.setId(rs.getLong(1));
                }
            }
        }
        return producto;
    }

    @Override
    public Producto leer(long id, Connection connection) throws SQLException {
        String sql = "SELECT p.*, cb.id as cb_id, cb.tipo, cb.valor, cb.fecha_asignacion, cb.observaciones "
                + "FROM productos p "
                + "LEFT JOIN codigos_barras cb ON p.codigos_barras_id = cb.id "
                + "WHERE p.id = ? AND p.eliminado = false";
        Producto producto = null;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    producto = new Producto();
                    producto.setId(rs.getLong("id"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setMarca(rs.getString("marca"));
                    producto.setCategoria(rs.getString("categoria"));
                    producto.setPrecio(rs.getDouble("precio"));
                    producto.setPeso(rs.getDouble("peso"));
                    long codigoBarrasId = rs.getLong("cb_id");
                    
                    if (!rs.wasNull()) { // Verificamos si el ID del código no era NULL
                        CodigoBarras codigoBarras = new CodigoBarras();
                        codigoBarras.setId(codigoBarrasId);
                        codigoBarras.setTipo(TipoCodigoBarras.valueOf(rs.getString("tipo")));
                        codigoBarras.setValor(rs.getString("valor"));
                        codigoBarras.setFechaAsignacion(rs.getDate("fecha_asignacion").toLocalDate());
                        codigoBarras.setObservaciones(rs.getString("observaciones"));
                        producto.setCodigoBarras(codigoBarras);
                    }
                }
            }
        }
        return producto;
    }

    @Override
    public List<Producto> leerTodos(Connection connection) throws SQLException {
        String sql = "SELECT p.*, cb.id as cb_id, cb.tipo, cb.valor, cb.fecha_asignacion, cb.observaciones "
                + "FROM productos p "
                + "LEFT JOIN codigos_barras cb ON p.codigos_barras_id = cb.id "
                + "WHERE p.eliminado = false";
        List<Producto> productos = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getLong("id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setMarca(rs.getString("marca"));
                producto.setCategoria(rs.getString("categoria"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setPeso(rs.getDouble("peso"));
                long codigoBarrasId = rs.getLong("cb_id");
                if (!rs.wasNull()) {
                    CodigoBarras codigoBarras = new CodigoBarras();
                    codigoBarras.setId(codigoBarrasId);
                    codigoBarras.setTipo(TipoCodigoBarras.valueOf(rs.getString("tipo")));
                    codigoBarras.setValor(rs.getString("valor"));
                    codigoBarras.setFechaAsignacion(rs.getDate("fecha_asignacion").toLocalDate());
                    codigoBarras.setObservaciones(rs.getString("observaciones"));
                    producto.setCodigoBarras(codigoBarras);
                }
                productos.add(producto);
            }
        }
        return productos;
    }

    @Override
    public void actualizar(Producto producto, Connection connection) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, marca = ?, categoria = ?, precio = ?, peso = ?, codigos_barras_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setString(2, producto.getMarca());
            ps.setString(3, producto.getCategoria());
            ps.setDouble(4, producto.getPrecio());
            if (producto.getPeso() != null) {
                ps.setDouble(5, producto.getPeso());
            } else {
                ps.setNull(5, Types.DOUBLE);
            }

            if (producto.getCodigoBarras() != null && producto.getCodigoBarras().getId() != null) {
                ps.setLong(6, producto.getCodigoBarras().getId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            ps.setLong(7, producto.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(long id, Connection connection) throws SQLException {
        String sql = "UPDATE productos SET eliminado = true WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    /**
     *
     * @param codigoBarrasId 
     * @param connection 
     * @return 
     * 
     */

    public List<Producto> buscarPorCodigoBarras(Long codigoBarrasId, Connection connection) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE codigos_barras_id = ? AND eliminado = false";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, codigoBarrasId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setId(rs.getLong("id"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setMarca(rs.getString("marca"));
                    producto.setCategoria(rs.getString("categoria"));
                    producto.setPrecio(rs.getDouble("precio"));
                    producto.setPeso(rs.getDouble("peso"));
                    if (rs.wasNull()) {
                        producto.setPeso(null);
                    }
                    producto.setEliminado(rs.getBoolean("eliminado"));
                    productos.add(producto);
                }
            }
        }
        return productos;
    }

    /**
     *
     * @param categoria 
     * @param connection 
     * @return 
     * 
     */
    public List<Producto> buscarPorCategoria(String categoria, Connection connection) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, cb.id as cb_id, cb.tipo, cb.valor, cb.fecha_asignacion, cb.observaciones "
                + "FROM productos p "
                + "LEFT JOIN codigos_barras cb ON p.codigos_barras_id = cb.id "
                + "WHERE LOWER(p.categoria) = ? AND p.eliminado = false";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categoria.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setId(rs.getLong("id"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setMarca(rs.getString("marca"));
                    producto.setCategoria(rs.getString("categoria"));
                    producto.setPrecio(rs.getDouble("precio"));
                    producto.setPeso(rs.getDouble("peso"));
                    if (rs.wasNull()) {
                        producto.setPeso(null);
                    }
                    producto.setEliminado(rs.getBoolean("eliminado"));

                    long codigoBarrasId = rs.getLong("cb_id");
                    if (!rs.wasNull()) {
                        CodigoBarras codigoBarras = new CodigoBarras();
                        codigoBarras.setId(codigoBarrasId);
                        codigoBarras.setTipo(TipoCodigoBarras.valueOf(rs.getString("tipo")));
                        codigoBarras.setValor(rs.getString("valor"));
                        codigoBarras.setFechaAsignacion(rs.getDate("fecha_asignacion").toLocalDate());
                        codigoBarras.setObservaciones(rs.getString("observaciones"));
                        producto.setCodigoBarras(codigoBarras);
                    }
                    productos.add(producto);
                }
            }
        }
        return productos;
    }
}
