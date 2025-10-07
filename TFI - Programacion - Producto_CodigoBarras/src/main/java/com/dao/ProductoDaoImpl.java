// ProductoDaoImpl.java
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
        String sql = "INSERT INTO productos (nombre, marca, categoria, precio, peso, eliminado, codigo_barras_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
            ps.setLong(7, producto.getCodigoBarras().getId()); // Asumimos que el CodigoBarras ya tiene ID

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
        // Usamos un JOIN para traer también los datos del código de barras asociado
        String sql = "SELECT p.*, cb.tipo, cb.valor, cb.fecha_asignacion, cb.observaciones " +
                     "FROM productos p " +
                     "JOIN codigos_barras cb ON p.codigo_barras_id = cb.id " +
                     "WHERE p.id = ? AND p.eliminado = false";
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
                    if (rs.wasNull()) { // El peso puede ser nulo
                        producto.setPeso(null);
                    }
                    producto.setEliminado(rs.getBoolean("eliminado"));

                    // Construimos el objeto CodigoBarras asociado
                    CodigoBarras codigoBarras = new CodigoBarras();
                    codigoBarras.setId(rs.getLong("codigo_barras_id"));
                    codigoBarras.setTipo(TipoCodigoBarras.valueOf(rs.getString("tipo")));
                    codigoBarras.setValor(rs.getString("valor"));
                    codigoBarras.setFechaAsignacion(rs.getDate("fecha_asignacion").toLocalDate());
                    codigoBarras.setObservaciones(rs.getString("observaciones"));
                    
                    producto.setCodigoBarras(codigoBarras);
                }
            }
        }
        return producto;
    }

    @Override
    public List<Producto> leerTodos(Connection connection) throws SQLException {
        String sql = "SELECT p.*, cb.tipo, cb.valor, cb.fecha_asignacion, cb.observaciones " +
                     "FROM productos p " +
                     "JOIN codigos_barras cb ON p.codigo_barras_id = cb.id " +
                     "WHERE p.eliminado = false";
        List<Producto> productos = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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

                    CodigoBarras codigoBarras = new CodigoBarras();
                    codigoBarras.setId(rs.getLong("codigo_barras_id"));
                    codigoBarras.setTipo(TipoCodigoBarras.valueOf(rs.getString("tipo")));
                    codigoBarras.setValor(rs.getString("valor"));
                    codigoBarras.setFechaAsignacion(rs.getDate("fecha_asignacion").toLocalDate());
                    codigoBarras.setObservaciones(rs.getString("observaciones"));
                    
                    producto.setCodigoBarras(codigoBarras);
                    productos.add(producto);
            }
        }
        return productos;
    }

    @Override
    public void actualizar(Producto producto, Connection connection) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, marca = ?, categoria = ?, precio = ?, peso = ?, codigo_barras_id = ? WHERE id = ?";
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
            ps.setLong(6, producto.getCodigoBarras().getId());
            ps.setLong(7, producto.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(long id, Connection connection) throws SQLException {
        // La baja del código de barras asociado se debe manejar en la capa de servicio si es necesario
        String sql = "UPDATE productos SET eliminado = true WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}