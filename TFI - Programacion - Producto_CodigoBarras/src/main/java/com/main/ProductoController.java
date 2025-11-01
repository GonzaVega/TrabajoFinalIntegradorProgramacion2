<<<<<<< HEAD
=======
//ProductoController.java
>>>>>>> 3cd61faa9efe151ceb63dd4b1acd3bd2a14a7765
package com.main;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.ArrayList;

import com.entities.CodigoBarras;
import com.entities.Producto;
import com.entities.TipoCodigoBarras;
import com.servicios.CodigoBarrasService;
import com.servicios.ProductoService;

public class ProductoController {
  private ProductoService productoService;
  private CodigoBarrasService codigoBarrasService;
  private CodigoBarrasController codigoBarrasController;
  private Scanner scanner;

  public ProductoController() {
    productoService = new ProductoService();
    codigoBarrasService = new CodigoBarrasService();
    codigoBarrasController = new CodigoBarrasController();
    scanner = new Scanner(System.in);
  }

  private Producto buscaValidaProducto(long idProducto) {
    Producto producto = productoService.getById(idProducto);
    if (producto == null) System.out.println("❌ No se encontró un producto con el ID proporcionado.");
    return producto;
  }

  private CodigoBarras seleccionarCodigoBarrasExistente() {
    System.out.println("Ingrese el ID del código de barras:");
    long idCodigoBarras = Long.parseLong(scanner.nextLine());
    CodigoBarras codigo = codigoBarrasService.getById(idCodigoBarras);
    
    if (codigo == null) System.out.println("❌ No se encontró el código de barras especificado");
    
    return codigo;
  }

  private CodigoBarras crearNuevoCodigoBarras() {
    System.out.println("--Creación rápida de código de barras--");
    System.out.println("Valor:");
    String valor = scanner.nextLine();
    System.out.println("Observaciones:");
    String observaciones = scanner.nextLine();
    
    codigoBarrasController.mostrarTiposCodigoBarras();
    TipoCodigoBarras tipoSeleccionado = codigoBarrasController.seleccionarTipoCodigoBarras();
    
    if (tipoSeleccionado == null) {
        System.out.println("❌ Tipo inválido. Código no creado.");
        return null;
    }
    
    CodigoBarras nuevoCodigo = new CodigoBarras();
    nuevoCodigo.setValor(valor);
    nuevoCodigo.setObservaciones(observaciones);
    nuevoCodigo.setFechaAsignacion(LocalDate.now());
    nuevoCodigo.setTipo(tipoSeleccionado);
    
    CodigoBarras creado = codigoBarrasService.insertar(nuevoCodigo);
    if (creado != null) {
        System.out.println("✅ Código de barras creado: " + creado);
    }
    
    return creado;
  }

  private CodigoBarras seleccionarCodigoBarrasCompleto() {
    System.out.println("¿Desea asignar un código de barras al producto? (S/N):");
    String asignarCodigo = scanner.nextLine().toUpperCase().trim();
    
    if (!asignarCodigo.equals("S")) return null;
    
    System.out.println("Opciones:");
    System.out.println("1) Seleccionar código de barras existente por ID");
    System.out.println("2) Crear nuevo código de barras");
    System.out.println("Seleccione opción (1-2):");
    
    String opcion = scanner.nextLine().trim();
    
    switch (opcion) {
        case "1":
            return seleccionarCodigoBarrasExistente();
            
        case "2":
            return crearNuevoCodigoBarras();
            
        default:
            System.out.println("Opción inválida. Sin código de barras asignado.");
            return null;
    }
  }

  public void crearProducto() {
    System.out.println("--Creación de nuevo producto--");
    System.out.println("Por favor, ingrese los datos del producto:");
    System.out.println("Nombre:");
    String nombre = scanner.nextLine();
    System.out.println("Marca:");
    String marca = scanner.nextLine();
    System.out.println("Categoría:");
    String categoria = scanner.nextLine();
    System.out.println("Precio:");
    double precio = Double.parseDouble(scanner.nextLine());
    System.out.println("Peso:");
    Double peso = Double.parseDouble(scanner.nextLine());

    CodigoBarras codigo = seleccionarCodigoBarrasCompleto();
    
    Producto nuevoProducto = new Producto(nombre, marca, categoria, precio, peso);
    if (codigo != null) nuevoProducto.setCodigoBarras(codigo);
    
    Producto creado = productoService.insertar(nuevoProducto);
    if (creado != null) {
      System.out.println("✅ Se creó correctamente su producto: " + creado);
      return;
    }
  }
  
  public void editarProducto() {
    System.out.println("--Edición de producto--");
    System.out.println("Por favor, ingrese el ID del producto a editar:");
    long idProducto = Long.parseLong(scanner.nextLine());
    
    Producto productoEditar = buscaValidaProducto(idProducto);
    if (productoEditar == null) return;
    
    System.out.println("Producto encontrado: " + productoEditar);
    System.out.println("Ingrese los nuevos datos del producto (deje en blanco para mantener el valor actual):");
    
    System.out.println("Nombre (" + productoEditar.getNombre() + "):");
    String nombre = scanner.nextLine();
    if (!nombre.isEmpty()) productoEditar.setNombre(nombre);
    
    System.out.println("Marca (" + productoEditar.getMarca() + "):");
    String marca = scanner.nextLine();
    if (!marca.isEmpty()) productoEditar.setMarca(marca);
    
    System.out.println("Categoría (" + productoEditar.getCategoria() + "):");
    String categoria = scanner.nextLine();
    if (!categoria.isEmpty()) productoEditar.setCategoria(categoria);
    
    System.out.println("Precio (" + productoEditar.getPrecio() + "):");
    String precioInput = scanner.nextLine();
    if (!precioInput.isEmpty()) {
      double precio = Double.parseDouble(precioInput);
      productoEditar.setPrecio(precio);
    }
    
    System.out.println("Peso (" + productoEditar.getPeso() + "):");
    String pesoInput = scanner.nextLine();
    if (!pesoInput.isEmpty()) {
      double peso = Double.parseDouble(pesoInput);
      productoEditar.setPeso(peso);
    }
    
    System.out.println("¿Desea modificar el código de barras? (S/N):");
    String modificarCodigo = scanner.nextLine().toUpperCase().trim();
    
    if (modificarCodigo.equals("S")) {
        System.out.println("Código de barras actual: " + 
            (productoEditar.getCodigoBarras() != null ? 
             productoEditar.getCodigoBarras().toString() : "Sin código asignado"));
        
        System.out.println("Opciones:");
        System.out.println("1) Asignar nuevo código de barras");
        System.out.println("2) Quitar código de barras actual");
        System.out.println("3) Mantener actual");
        System.out.println("Seleccione opción (1-3):");
        
        String opcion = scanner.nextLine().trim();
        
        switch (opcion) {
            case "1":
                CodigoBarras nuevoCodigo = seleccionarCodigoBarrasCompleto();
                if (nuevoCodigo != null) {
                    productoEditar.setCodigoBarras(nuevoCodigo);
                    System.out.println("✅ Código de barras asignado correctamente");
                }
                break;
                
            case "2":
                productoEditar.setCodigoBarras(null);
                System.out.println("✅ Código de barras removido del producto");
                break;
                
            case "3":
                System.out.println("Código de barras mantenido sin cambios");
                break;
                
            default:
                System.out.println("Opción inválida. Código de barras mantenido sin cambios");
                break;
        }
    }
    
    Producto editado = productoService.actualizar(productoEditar);
    if (editado != null) {
      System.out.println("✅ Producto actualizado correctamente: " + editado);
      return;
    }
  }

  public void buscarProducto() {
    System.out.println("--Búsqueda de producto--");
    System.out.println("Por favor, ingrese el ID del producto a buscar:");
    long idProducto = Long.parseLong(scanner.nextLine());
    
    Producto productoEncontrado = buscaValidaProducto(idProducto);
    if (productoEncontrado != null) {
        System.out.println("✅ Producto encontrado: " + productoEncontrado);
        return;
    }
  }

  public void mostrarProductos() {
    System.out.println("--Lista de productos--");
    ArrayList<Producto> productos = (ArrayList<Producto>) productoService.getAll();
    if (productos.isEmpty()) System.out.println("❌ No hay productos registrados.");
        
    for (Producto p : productos) {
        System.out.println(p);
    }
  }

  //TODO: habilitar este método cuando este el DAO.
  // public void mostrarProductosCategoria() {
  //   System.out.println("--Productos por Categoría--");
  //   System.out.println("Por favor, ingrese la categoría a filtrar:");
  //   String categoria = scanner.nextLine().trim().toLowerCase();
  //   if (categoria == null || categoria.isEmpty()) {
  //       System.out.println("❌ Categoría inválida. Operación cancelada.");
  //       return;
  //   }

  //   ArrayList<Producto> productos = (ArrayList<Producto>) productoService.buscarPorCategoria(categoria);
  //   if (productos.isEmpty()) System.out.println("❌ No hay productos registrados para la categoría seleccionada.");

  //   for (Producto p : productos) {
  //       System.out.println(p);
  //   }
  // }

  public void eliminarProducto() {
    System.out.println("--Eliminación de producto--");
    System.out.println("Por favor, ingrese el ID del producto a eliminar:");
    long idProducto = Long.parseLong(scanner.nextLine());
    
    Producto productoEliminar = buscaValidaProducto(idProducto);
    if (productoEliminar == null) return;
    
    Producto eliminado = productoService.eliminar(idProducto);
    if (eliminado != null) {
      System.out.println("✅ Producto eliminado correctamente: " + eliminado);
      return;
    }
  }

  public void asignarCodigo() {
    System.out.println("--Asignación de código de barras a producto--");
    System.out.println("Por favor, ingrese el ID del producto:");
    long idProducto = Long.parseLong(scanner.nextLine());
    
    Producto productoExistente = buscaValidaProducto(idProducto);
    if (productoExistente == null) return;
    
    System.out.println("Producto para asignar nuevo código: " + productoExistente);
    
    CodigoBarras codigo = seleccionarCodigoBarrasCompleto();
    if (codigo == null) {
        System.out.println("❌ Operación cancelada - no se seleccionó código de barras válido");
        return;
    }
    
    productoExistente.setCodigoBarras(codigo);
    Producto actualizado = productoService.actualizar(productoExistente);
    if (actualizado != null) {
        System.out.println("✅ Código de barras asignado correctamente al producto: " + actualizado);
        return;
    }
  }
}