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
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void crearInterfaz() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(crearBoton(" Crear Producto", new Color(40, 167, 69), this::crearProducto));
        panel.add(crearBoton(" Crear Código de Barras", new Color(40, 167, 69), this::crearCodigoBarras));
        panel.add(crearBoton(" Listar Productos", new Color(0, 123, 255), this::listarProductos));
        panel.add(crearBoton(" Listar Códigos", new Color(0, 123, 255), this::listarCodigosBarras));
        panel.add(crearBoton(" Buscar Producto por ID", new Color(23, 162, 184), this::buscarProducto));
        panel.add(crearBoton(" Buscar Código por ID", new Color(23, 162, 184), this::buscarCodigoBarras));
        panel.add(crearBoton(" Buscar por Categoría", new Color(108, 117, 125), this::buscarPorCategoria));
        panel.add(crearBoton(" Buscar por Tipo", new Color(108, 117, 125), this::buscarPorTipo));
        panel.add(crearBoton(" Editar Producto", new Color(255, 193, 7), this::editarProducto));
        panel.add(crearBoton(" Editar Código", new Color(255, 193, 7), this::editarCodigoBarras));
        panel.add(crearBoton(" Asignar Código a Producto", new Color(111, 66, 193), this::asignarCodigoAProducto));
        panel.add(crearBoton(" Eliminar Producto", new Color(220, 53, 69), this::eliminarProducto));
        panel.add(crearBoton(" Eliminar Código", new Color(220, 53, 69), this::eliminarCodigoBarras));
        panel.add(crearBoton(" Salir", new Color(52, 58, 64), () -> System.exit(0)));

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

   private void crearProducto() {
        JTextField txtNombre = new JTextField(20);
        JTextField txtMarca = new JTextField(20);
        JTextField txtCategoria = new JTextField(20);
        JTextField txtPrecio = new JTextField(10);
        JTextField txtPeso = new JTextField(10);
        
        JCheckBox chkAsignarCodigo = new JCheckBox("¿Asignar código de barras?");

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
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
        form.add(new JLabel(""));
        form.add(chkAsignarCodigo);

        int result = JOptionPane.showConfirmDialog(this, form, "Crear Producto", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String nombre = txtNombre.getText().trim();
                String marca = txtMarca.getText().trim();
                String categoria = txtCategoria.getText().trim();
                String precioStr = txtPrecio.getText().trim();

                if (nombre.isEmpty() || marca.isEmpty() || categoria.isEmpty() || precioStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, " Nombre, Marca, Categoría y Precio son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double precio = Double.parseDouble(precioStr);
                if (precio <= 0) {
                    JOptionPane.showMessageDialog(this, " El precio debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Producto p = new Producto();
                p.setNombre(nombre);
                p.setMarca(marca);
                p.setCategoria(categoria);
                p.setPrecio(precio);

                String pesoStr = txtPeso.getText().trim();
                if (!pesoStr.isEmpty()) {
                    double peso = Double.parseDouble(pesoStr);
                    if (peso < 0) {
                        JOptionPane.showMessageDialog(this, " El peso no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    p.setPeso(peso);
                }

                if (chkAsignarCodigo.isSelected()) {
                    CodigoBarras codigo = seleccionarOCrearCodigoBarras();
                    if (codigo != null) {
                        p.setCodigoBarras(codigo);
                    }
                }

                Producto resultado = productoService.insertar(p);
                if (resultado != null) {
                    JOptionPane.showMessageDialog(this, " Producto creado con ID: " + resultado.getId());
                } else {
                    JOptionPane.showMessageDialog(this, " Error al crear producto", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, " Precio/Peso inválido", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, " Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private CodigoBarras seleccionarOCrearCodigoBarras() {
        String[] opciones = {"Seleccionar existente", "Crear nuevo", "Cancelar"};
        int opcion = JOptionPane.showOptionDialog(this, "¿Cómo desea asignar el código de barras?",
                "Asignar Código", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);

        if (opcion == 0) {
            return seleccionarCodigoExistente();
        } else if (opcion == 1) {
            return crearCodigoBarrasRapido();
        }
        return null;
    }

    private CodigoBarras seleccionarCodigoExistente() {
        List<CodigoBarras> codigos = codigoBarrasService.getAll();
        
        if (codigos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay códigos de barras disponibles");
            return null;
        }

        String[] opciones = new String[codigos.size()];
        for (int i = 0; i < codigos.size(); i++) {
            CodigoBarras cb = codigos.get(i);
            opciones[i] = String.format("ID: %d - %s (%s)", cb.getId(), cb.getValor(), cb.getTipo());
        }

        String seleccion = (String) JOptionPane.showInputDialog(this, "Seleccione un código:",
                "Códigos Disponibles", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion != null) {
            String idStr = seleccion.split(" ")[1];
            long id = Long.parseLong(idStr);
            return codigoBarrasService.getById(id);
        }
        return null;
    }

    private CodigoBarras crearCodigoBarrasRapido() {
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

        int result = JOptionPane.showConfirmDialog(this, form, "Crear Código Rápido", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String valor = txtValor.getText().trim();
                TipoCodigoBarras tipo = (TipoCodigoBarras) cboTipo.getSelectedItem();

                if (!validarFormatoCodigoBarras(valor, tipo)) {
                    return null;
                }

                CodigoBarras cb = new CodigoBarras();
                cb.setValor(valor);
                cb.setTipo(tipo);
                cb.setFechaAsignacion(LocalDate.now());
                cb.setObservaciones(txtObservaciones.getText().trim());

                CodigoBarras resultado = codigoBarrasService.insertar(cb);
                
                if (resultado == null) {
                    JOptionPane.showMessageDialog(this, 
                        " No se pudo crear el código de barras.\n\n" +
                        "Posibles causas:\n" +
                        "• El código ya existe en la base de datos\n" +
                        "• Error de conexión\n\n" +
                        "Puede seleccionar un código existente o intentar con otro valor.", 
                        "Error al Crear Código", 
                        JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                
                return resultado;
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    " Error inesperado: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    private boolean validarFormatoCodigoBarras(String valor, TipoCodigoBarras tipo) {
        switch (tipo) {
            case EAN13:
                if (!valor.matches("\\d{13}")) {
                    JOptionPane.showMessageDialog(this, " EAN-13 debe tener exactamente 13 dígitos", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;
            case EAN8:
                if (!valor.matches("\\d{8}")) {
                    JOptionPane.showMessageDialog(this, " EAN-8 debe tener exactamente 8 dígitos", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;
            case UPC:
                if (!valor.matches("\\d{12}")) {
                    JOptionPane.showMessageDialog(this, " UPC debe tener exactamente 12 dígitos", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;
            default:
                if (valor.isEmpty()) {
                    JOptionPane.showMessageDialog(this, " El valor no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                break;
        }
        return true;
    }

    private void asignarCodigoAProducto() {
        String idStr = JOptionPane.showInputDialog(this, "Ingrese ID del producto:");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                long id = Long.parseLong(idStr.trim());
                Producto p = productoService.getById(id);

                if (p == null) {
                    JOptionPane.showMessageDialog(this, " Producto no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String info = String.format("Producto: %s\nCódigo actual: %s",
                        p.getNombre(),
                        p.getCodigoBarras() != null ? p.getCodigoBarras().getValor() : "Sin código");

                JOptionPane.showMessageDialog(this, info, "Producto Seleccionado", JOptionPane.INFORMATION_MESSAGE);

                CodigoBarras codigo = seleccionarOCrearCodigoBarras();
                if (codigo != null) {
                    p.setCodigoBarras(codigo);
                    Producto resultado = productoService.actualizar(p);
                    if (resultado != null) {
                        JOptionPane.showMessageDialog(this, " Código de barras asignado correctamente");
                    } else {
                        JOptionPane.showMessageDialog(this, " Error al asignar código", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, " ID inválido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listarProductos() {
        List<Producto> productos = productoService.getAll();

        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos registrados");
            return;
        }

        String[] columnas = {"ID", "Nombre", "Marca", "Categoría", "Precio", "Peso", "Código"};
        Object[][] datos = new Object[productos.size()][7];

        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            datos[i][0] = p.getId();
            datos[i][1] = p.getNombre();
            datos[i][2] = p.getMarca();
            datos[i][3] = p.getCategoria();
            datos[i][4] = String.format("$%.2f", p.getPrecio());
            datos[i][5] = p.getPeso() != null ? String.format("%.2f kg", p.getPeso()) : "N/A";
            datos[i][6] = p.getCodigoBarras() != null ? p.getCodigoBarras().getValor() : "Sin código";
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
                            "ID: %d\nNombre: %s\nMarca: %s\nCategoría: %s\nPrecio: $%.2f\nPeso: %s\nCódigo de Barras: %s",
                            p.getId(), p.getNombre(), p.getMarca(), p.getCategoria(), p.getPrecio(),
                            p.getPeso() != null ? p.getPeso() + " kg" : "N/A",
                            p.getCodigoBarras() != null ? p.getCodigoBarras().getValor() + " (" + p.getCodigoBarras().getTipo() + ")" : "Sin código"
                    );
                    JOptionPane.showMessageDialog(this, info, "Producto Encontrado", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, " Producto no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, " ID inválido", "Error", JOptionPane.ERROR_MESSAGE);
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
                        JOptionPane.showMessageDialog(this, " Producto eliminado");
                    } else {
                        JOptionPane.showMessageDialog(this, " Error al eliminar", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, " ID inválido", "Error", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(this, " Producto no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JTextField txtNombre = new JTextField(p.getNombre(), 20);
                JTextField txtMarca = new JTextField(p.getMarca(), 20);
                JTextField txtCategoria = new JTextField(p.getCategoria(), 20);
                JTextField txtPrecio = new JTextField(String.valueOf(p.getPrecio()), 10);
                JTextField txtPeso = new JTextField(p.getPeso() != null ? String.valueOf(p.getPeso()) : "", 10);
                
                JCheckBox chkModificarCodigo = new JCheckBox("¿Modificar código de barras?");

                JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
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
                form.add(new JLabel("Código actual: " + (p.getCodigoBarras() != null ? p.getCodigoBarras().getValor() : "Sin código")));
                form.add(chkModificarCodigo);

                int result = JOptionPane.showConfirmDialog(this, form, "Editar Producto", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    p.setNombre(txtNombre.getText().trim());
                    p.setMarca(txtMarca.getText().trim());
                    p.setCategoria(txtCategoria.getText().trim());
                    p.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));

                    String pesoStr = txtPeso.getText().trim();
                    p.setPeso(!pesoStr.isEmpty() ? Double.parseDouble(pesoStr) : null);

                    if (chkModificarCodigo.isSelected()) {
                        CodigoBarras codigo = seleccionarOCrearCodigoBarras();
                        p.setCodigoBarras(codigo);
                    }

                    Producto resultado = productoService.actualizar(p);
                    if (resultado != null) {
                        JOptionPane.showMessageDialog(this, " Producto actualizado");
                    } else {
                        JOptionPane.showMessageDialog(this, " Error al actualizar", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, " Datos inválidos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

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
                String valor = txtValor.getText().trim();
                TipoCodigoBarras tipo = (TipoCodigoBarras) cboTipo.getSelectedItem();

                if (!validarFormatoCodigoBarras(valor, tipo)) {
                    return;
                }

                CodigoBarras cb = new CodigoBarras();
                cb.setValor(valor);
                cb.setTipo(tipo);
                cb.setFechaAsignacion(LocalDate.now());
                cb.setObservaciones(txtObservaciones.getText().trim());

                CodigoBarras resultado = codigoBarrasService.insertar(cb);
                
                if (resultado != null) {
                    JOptionPane.showMessageDialog(this, " Código creado con ID: " + resultado.getId());
                } else {
                    JOptionPane.showMessageDialog(this, 
                        " No se pudo crear el código de barras.\n\n" +
                        "Posibles causas:\n" +
                        "• El código '" + valor + "' ya existe en la base de datos\n" +
                        "• Error de conexión a la base de datos", 
                        "Error al Crear Código", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, " Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(this, " Código no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, " ID inválido", "Error", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(this, " Código no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
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
                    String valor = txtValor.getText().trim();
                    TipoCodigoBarras tipo = (TipoCodigoBarras) cboTipo.getSelectedItem();

                    if (!validarFormatoCodigoBarras(valor, tipo)) {
                        return;
                    }

                    cb.setValor(valor);
                    cb.setTipo(tipo);
                    cb.setObservaciones(txtObservaciones.getText().trim());

                    CodigoBarras resultado = codigoBarrasService.actualizar(cb);
                    
                    if (resultado != null) {
                        JOptionPane.showMessageDialog(this, " Código actualizado");
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            " No se pudo actualizar el código de barras.\n\n" +
                            "Posibles causas:\n" +
                            "• El código '" + valor + "' ya existe para otro registro\n" +
                            "• Error de conexión a la base de datos", 
                            "Error al Actualizar", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, " Datos inválidos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

   private void eliminarCodigoBarras() {
        String idStr = JOptionPane.showInputDialog(this, "Ingrese ID del código a eliminar:");
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                long id = Long.parseLong(idStr.trim());
                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el código?", "Confirmar", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    CodigoBarras resultado = codigoBarrasService.eliminar(id);
                    if (resultado != null) {
                        JOptionPane.showMessageDialog(this, " Código eliminado");
                    } else {
                        JOptionPane.showMessageDialog(this, " Error al eliminar", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, " ID inválido", "Error", JOptionPane.ERROR_MESSAGE);
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