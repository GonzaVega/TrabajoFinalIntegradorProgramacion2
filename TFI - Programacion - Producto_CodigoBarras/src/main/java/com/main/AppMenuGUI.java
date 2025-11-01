package com.main;

import com.entities.CodigoBarras;
import com.entities.Producto;
import com.entities.TipoCodigoBarras;
import com.servicios.CodigoBarrasService;
import com.servicios.ProductoService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppMenuGUI extends JFrame {
    private ProductoService productoService;
    private CodigoBarrasService codigoBarrasService;

    public AppMenuGUI() {
        productoService = new ProductoService();
        codigoBarrasService = new CodigoBarrasService();

        configurarVentana();
        crearInterfaz();
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión - Modo Gráfico");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void crearInterfaz() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Botones principales
        panel.add(crearBoton("➕ Crear Producto", new Color(40, 167, 69), this::crearProducto));
        panel.add(crearBoton("➕ Crear Código de Barras", new Color(40, 167, 69), this::crearCodigoBarras));

        panel.add(crearBoton("📋 Listar Productos", new Color(0, 123, 255), this::listarProductos));
        panel.add(crearBoton("📋 Listar Códigos", new Color(0, 123, 255), this::listarCodigosBarras));

        panel.add(crearBoton("🔍 Buscar Producto por ID", new Color(23, 162, 184), this::buscarProducto));
        panel.add(crearBoton("🔍 Buscar Código por ID", new Color(23, 162, 184), this::buscarCodigoBarras));

        panel.add(crearBoton("📂 Buscar por Categoría", new Color(108, 117, 125), this::buscarPorCategoria));
        panel.add(crearBoton("📂 Buscar por Tipo", new Color(108, 117, 125), this::buscarPorTipo));

        panel.add(crearBoton("✏️ Editar Producto", new Color(255, 193, 7), this::editarProducto));
        panel.add(crearBoton("🗑️ Eliminar Producto", new Color(220, 53, 69), this::eliminarProducto));

        panel.add(crearBoton("✏️ Editar Código", new Color(255, 193, 7), this::editarCodigoBarras));
        panel.add(crearBoton("❌ Salir", new Color(52, 58, 64), () -> System.exit(0)));

        add(panel);
        setVisible(true);
    }

    private JButton crearBoton(String texto, Color color, Runnable accion) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> accion.run());
        return btn;
    }

    // ==================== PRODUCTOS ====================

    private void crearProducto() {
        JTextField txtNombre = new JTextField(20);
        JTextField txtMarca = new JTextField(20);
        JTextField txtCategoria = new JTextField(20);
        JTextField txtPrecio = new JTextField(10);
        JTextField txtPeso = new JTextField(10);

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Marca:"));
        form.add(txtMarca);
        form.add(new JLabel("Categoría:"));
        form.add(txtCategoria);
        form.add(new JLabel("Precio:"));
        form.add(txtPrecio);
        form.add(new JLabel("Peso (opcional):"));
        form.add(txtPeso);

        int result = JOptionPane.showConfirmDialog(this, form, "Crear Producto", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Producto p = new Producto();
                p.setNombre(txtNombre.getText().trim());
                p.setMarca(txtMarca.getText().trim());
                p.setCategoria(txtCategoria.getText().trim());
                p.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));

                String pesoStr = txtPeso.getText().trim();
                if (!pesoStr.isEmpty()) {
                    p.setPeso(Double.parseDouble(pesoStr));
                }

                Producto resultado = productoService.insertar(p);
                if (resultado != null) {
                    JOptionPane.showMessageDialog(this, "✅ Producto creado con ID: " + resultado.getId());
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Error al crear producto", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "❌ Precio/Peso inválido", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listarProductos() {
        List<Producto> productos = productoService.getAll();

        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos registrados");
            return;
        }

        String[] columnas = {"ID", "Nombre", "Marca", "Categoría", "Precio", "Peso"};
        Object[][] datos = new Object[productos.size()][6];

        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            datos[i][0] = p.getId();
            datos[i][1] = p.getNombre();
            datos[i][2] = p.getMarca();
            datos[i][3] = p.getCategoria();
            datos[i][4] = String.format("$%.2f", p.getPrecio());
            datos[i][5] = p.getPeso() != null ? String.format("%.2f kg", p.getPeso()) : "N/A";
        }

        JTable table = new JTable(datos, columnas);
        table.setEnabled(false);
        JScrollPane scroll = new JScrollPane(table);

        JOptionPane.showMessageDialog(this, scroll, "Productos", JOptionPane.PLAIN_MESSAGE);
    }

    private void buscarProducto() {
        String idStr = JOptionPane.showInputDialog(this, "Ingrese ID del producto:");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                long id = Long.parseLong(idStr.trim());
                Producto p = productoService.getById(id);

                if (p != null) {
                    String info = String.format(
                            "ID: %d\nNombre: %s\nMarca: %s\nCategoría: %s\nPrecio: $%.2f\nPeso: %s",
                            p.getId(), p.getNombre(), p.getMarca(), p.getCategoria(), p.getPrecio(),
                            p.getPeso() != null ? p.getPeso() + " kg" : "N/A"
                    );
                    JOptionPane.showMessageDialog(this, info, "Producto Encontrado", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Producto no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "❌ ID inválido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarPorCategoria() {
        String categoria = JOptionPane.showInputDialog(this, "Ingrese categoría:");
        if (categoria != null && !categoria.trim().isEmpty()) {
            List<Producto> productos = productoService.buscarPorCategoria(categoria.trim());

            if (productos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron productos en esa categoría");
                return;
            }

            String[] columnas = {"ID", "Nombre", "Marca", "Precio"};
            Object[][] datos = new Object[productos.size()][4];

            for (int i = 0; i < productos.size(); i++) {
                Producto p = productos.get(i);
                datos[i][0] = p.getId();
                datos[i][1] = p.getNombre();
                datos[i][2] = p.getMarca();
                datos[i][3] = String.format("$%.2f", p.getPrecio());
            }

            JTable table = new JTable(datos, columnas);
            table.setEnabled(false);
            JScrollPane scroll = new JScrollPane(table);

            JOptionPane.showMessageDialog(this, scroll, "Productos - " + categoria, JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void eliminarProducto() {
        String idStr = JOptionPane.showInputDialog(this, "Ingrese ID del producto a eliminar:");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                long id = Long.parseLong(idStr.trim());
                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el producto?", "Confirmar", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    Producto resultado = productoService.eliminar(id);
                    if (resultado != null) {
                        JOptionPane.showMessageDialog(this, "✅ Producto eliminado");
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ Error al eliminar", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "❌ ID inválido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarProducto() {
        String idStr = JOptionPane.showInputDialog(this, "Ingrese ID del producto a editar:");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                long id = Long.parseLong(idStr.trim());
                Producto p = productoService.getById(id);

                if (p == null) {
                    JOptionPane.showMessageDialog(this, "❌ Producto no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JTextField txtNombre = new JTextField(p.getNombre(), 20);
                JTextField txtMarca = new JTextField(p.getMarca(), 20);
                JTextField txtCategoria = new JTextField(p.getCategoria(), 20);
                JTextField txtPrecio = new JTextField(String.valueOf(p.getPrecio()), 10);
                JTextField txtPeso = new JTextField(p.getPeso() != null ? String.valueOf(p.getPeso()) : "", 10);

                JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
                form.add(new JLabel("Nombre:"));
                form.add(txtNombre);
                form.add(new JLabel("Marca:"));
                form.add(txtMarca);
                form.add(new JLabel("Categoría:"));
                form.add(txtCategoria);
                form.add(new JLabel("Precio:"));
                form.add(txtPrecio);
                form.add(new JLabel("Peso:"));
                form.add(txtPeso);

                int result = JOptionPane.showConfirmDialog(this, form, "Editar Producto", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    p.setNombre(txtNombre.getText().trim());
                    p.setMarca(txtMarca.getText().trim());
                    p.setCategoria(txtCategoria.getText().trim());
                    p.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));

                    String pesoStr = txtPeso.getText().trim();
                    p.setPeso(!pesoStr.isEmpty() ? Double.parseDouble(pesoStr) : null);

                    Producto resultado = productoService.actualizar(p);
                    if (resultado != null) {
                        JOptionPane.showMessageDialog(this, "✅ Producto actualizado");
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ Error al actualizar", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "❌ Datos inválidos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==================== CÓDIGOS DE BARRAS ====================

    private void crearCodigoBarras() {
        JTextField txtValor = new JTextField(20);
        JComboBox<TipoCodigoBarras> cboTipo = new JComboBox<>(TipoCodigoBarras.values());
        JTextField txtObservaciones = new JTextField(20);

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.add(new JLabel("Valor:"));
        form.add(txtValor);
        form.add(new JLabel("Tipo:"));
        form.add(cboTipo);
        form.add(new JLabel("Observaciones:"));
        form.add(txtObservaciones);

        int result = JOptionPane.showConfirmDialog(this, form, "Crear Código de Barras", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                CodigoBarras cb = new CodigoBarras();
                cb.setValor(txtValor.getText().trim());
                cb.setTipo((TipoCodigoBarras) cboTipo.getSelectedItem());
                cb.setFechaAsignacion(LocalDate.now());
                cb.setObservaciones(txtObservaciones.getText().trim());

                CodigoBarras resultado = codigoBarrasService.insertar(cb);
                if (resultado != null) {
                    JOptionPane.showMessageDialog(this, "✅ Código creado con ID: " + resultado.getId());
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Error al crear código", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listarCodigosBarras() {
        List<CodigoBarras> codigos = codigoBarrasService.getAll();

        if (codigos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay códigos registrados");
            return;
        }

        String[] columnas = {"ID", "Valor", "Tipo", "Fecha"};
        Object[][] datos = new Object[codigos.size()][4];

        for (int i = 0; i < codigos.size(); i++) {
            CodigoBarras cb = codigos.get(i);
            datos[i][0] = cb.getId();
            datos[i][1] = cb.getValor();
            datos[i][2] = cb.getTipo();
            datos[i][3] = cb.getFechaAsignacion() != null ? cb.getFechaAsignacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
        }

        JTable table = new JTable(datos, columnas);
        table.setEnabled(false);
        JScrollPane scroll = new JScrollPane(table);

        JOptionPane.showMessageDialog(this, scroll, "Códigos de Barras", JOptionPane.PLAIN_MESSAGE);
    }

    private void buscarCodigoBarras() {
        String idStr = JOptionPane.showInputDialog(this, "Ingrese ID del código:");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                long id = Long.parseLong(idStr.trim());
                CodigoBarras cb = codigoBarrasService.getById(id);

                if (cb != null) {
                    String info = String.format(
                            "ID: %d\nValor: %s\nTipo: %s\nFecha: %s\nObservaciones: %s",
                            cb.getId(), cb.getValor(), cb.getTipo(),
                            cb.getFechaAsignacion() != null ? cb.getFechaAsignacion() : "N/A",
                            cb.getObservaciones() != null ? cb.getObservaciones() : "N/A"
                    );
                    JOptionPane.showMessageDialog(this, info, "Código Encontrado", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Código no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "❌ ID inválido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarCodigoBarras() {
        String idStr = JOptionPane.showInputDialog(this, "Ingrese ID del código a editar:");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                long id = Long.parseLong(idStr.trim());
                CodigoBarras cb = codigoBarrasService.getById(id);

                if (cb == null) {
                    JOptionPane.showMessageDialog(this, "❌ Código no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JTextField txtValor = new JTextField(cb.getValor(), 20);
                JComboBox<TipoCodigoBarras> cboTipo = new JComboBox<>(TipoCodigoBarras.values());
                cboTipo.setSelectedItem(cb.getTipo());
                JTextField txtObservaciones = new JTextField(cb.getObservaciones() != null ? cb.getObservaciones() : "", 20);

                JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
                form.add(new JLabel("Valor:"));
                form.add(txtValor);
                form.add(new JLabel("Tipo:"));
                form.add(cboTipo);
                form.add(new JLabel("Observaciones:"));
                form.add(txtObservaciones);

                int result = JOptionPane.showConfirmDialog(this, form, "Editar Código de Barras", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    cb.setValor(txtValor.getText().trim());
                    cb.setTipo((TipoCodigoBarras) cboTipo.getSelectedItem());
                    cb.setObservaciones(txtObservaciones.getText().trim());

                    CodigoBarras resultado = codigoBarrasService.actualizar(cb);
                    if (resultado != null) {
                        JOptionPane.showMessageDialog(this, "✅ Código actualizado");
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ Error al actualizar", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "❌ Datos inválidos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarPorTipo() {
        TipoCodigoBarras tipo = (TipoCodigoBarras) JOptionPane.showInputDialog(
                this, "Seleccione tipo:", "Buscar por Tipo",
                JOptionPane.QUESTION_MESSAGE, null,
                TipoCodigoBarras.values(), TipoCodigoBarras.EAN13
        );

        if (tipo != null) {
            List<CodigoBarras> codigos = codigoBarrasService.buscarPorTipo(tipo);

            if (codigos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron códigos de ese tipo");
                return;
            }

            String[] columnas = {"ID", "Valor", "Fecha"};
            Object[][] datos = new Object[codigos.size()][3];

            for (int i = 0; i < codigos.size(); i++) {
                CodigoBarras cb = codigos.get(i);
                datos[i][0] = cb.getId();
                datos[i][1] = cb.getValor();
                datos[i][2] = cb.getFechaAsignacion() != null ? cb.getFechaAsignacion() : "N/A";
            }

            JTable table = new JTable(datos, columnas);
            table.setEnabled(false);
            JScrollPane scroll = new JScrollPane(table);

            JOptionPane.showMessageDialog(this, scroll, "Códigos - " + tipo, JOptionPane.PLAIN_MESSAGE);
        }
    }
}