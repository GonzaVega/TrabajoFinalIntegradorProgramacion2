package com.main;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.List;

import com.entities.CodigoBarras;
import com.entities.Producto;
import com.entities.TipoCodigoBarras;
import com.servicios.CodigoBarrasService;
import com.servicios.ProductoService;
import com.utils.InputValidator;

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
    Long idCodigoBarras = InputValidator.leerLongSeguro(scanner, "Ingrese el ID del código de barras:");
    CodigoBarras codigo = codigoBarrasService.getById(idCodigoBarras);
    
    if (codigo == null) System.out.println("❌ No se encontró el código de barras especificado");
    
    return codigo;
  }

  private CodigoBarras crearNuevoCodigoBarras() {
    System.out.println("--Creación rápida de código de barras--");
    
    try {
      String observaciones = InputValidator.leerString(scanner, "Observaciones (opcional):");
      
      codigoBarrasController.mostrarTiposCodigoBarras();
      TipoCodigoBarras tipoSeleccionado = codigoBarrasController.seleccionarTipoCodigoBarras();
      
      if (tipoSeleccionado == null) {
        System.out.println("❌ Tipo inválido. Código no creado.");
        return null;
      }

      String valor;
      switch (tipoSeleccionado) {
        case EAN13:
          valor = InputValidator.leerCodigoBarras(scanner, "Valor (13 dígitos):", 13);
          break;
        case EAN8:
          valor = InputValidator.leerCodigoBarras(scanner, "Valor (8 dígitos):", 8);
          break;
        case UPC:
          valor = InputValidator.leerCodigoBarras(scanner, "Valor (12 dígitos):", 12);
          break;
        default:
          valor = InputValidator.leerStringNoVacio(scanner, "Valor:");
          break;
      }
      
      CodigoBarras nuevoCodigo = new CodigoBarras();
      nuevoCodigo.setValor(valor);
      nuevoCodigo.setObservaciones(observaciones);
      nuevoCodigo.setFechaAsignacion(LocalDate.now());
      nuevoCodigo.setTipo(tipoSeleccionado);
      
      CodigoBarras creado = codigoBarrasService.insertar(nuevoCodigo);
      
      if (creado != null) {
        System.out.println("✅ Código de barras creado: " + creado);
        return creado;
      } else {
        System.out.println("❌ Error al crear el código de barras. Verifique los datos ingresados.");
        return null;
      }
      
    } catch (Exception e) {
      System.err.println("❌ Error inesperado al crear código de barras: " + e.getMessage());
      return null;
    }
  }

  private CodigoBarras seleccionarCodigoBarrasCompleto() {
    if (!InputValidator.leerConfirmacion(scanner, "¿Desea asignar un código de barras al producto?")) {
      return null;
    }
    
    System.out.println("Opciones:");
    System.out.println("1) Seleccionar código de barras existente por ID");
    System.out.println("2) Crear nuevo código de barras");
    
    Integer opcion = InputValidator.leerIntegerSeguro(scanner, "Seleccione opción (1-2):");
    
    switch (opcion) {
      case 1:
        return seleccionarCodigoBarrasExistente();
        
      case 2:
        return crearNuevoCodigoBarras();
        
      default:
        System.out.println("Opción inválida. Sin código de barras asignado.");
        return null;
    }
  }

  public void crearProducto() {
    System.out.println("--Creación de nuevo producto--");
    System.out.println("Por favor, ingrese los datos del producto:");
    String nombre = InputValidator.leerStringNoVacio(scanner, "Nombre:");
    String marca = InputValidator.leerStringNoVacio(scanner, "Marca:");
    String categoria = InputValidator.leerStringNoVacio(scanner, "Categoría:").toUpperCase();
    Double precio = InputValidator.leerDoublePositivo(scanner, "Precio:");
  
    System.out.println("¿Desea ingresar el peso del producto?");
    String ingresarPeso = InputValidator.leerStringMayusculas(scanner, "(S/N):");
    Double peso = null;
    if (ingresarPeso.equals("S")) {
      peso = InputValidator.leerDoubleNoNegativo(scanner, "Peso:");
    }

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
    Long idProducto = InputValidator.leerLongSeguro(scanner, "Ingrese el ID del producto a editar:");
  
    Producto productoEditar = buscaValidaProducto(idProducto);
    if (productoEditar == null) return;
  
    System.out.println("Producto encontrado: " + productoEditar);
    System.out.println("Ingrese los nuevos datos del producto (deje en blanco para mantener el valor actual):");
  
     String nombre = InputValidator.leerStringOpcional(scanner, "Nuevo nombre:", productoEditar.getNombre());
    productoEditar.setNombre(nombre);
  
    String marca = InputValidator.leerStringOpcional(scanner, "Nueva marca:", productoEditar.getMarca());
    productoEditar.setMarca(marca);
    
    String categoria = InputValidator.leerStringOpcional(scanner, "Nueva categoría:", productoEditar.getCategoria());
    productoEditar.setCategoria(categoria.toUpperCase());
    
    Double precio = InputValidator.leerDoublePositivoOpcional(scanner, "Nuevo precio:", productoEditar.getPrecio());
    productoEditar.setPrecio(precio);
    
    Double peso = InputValidator.leerDoubleNoNegativoOpcional(scanner, "Nuevo peso:", productoEditar.getPeso());
    productoEditar.setPeso(peso);
    
    if (InputValidator.leerConfirmacion(scanner, "¿Desea modificar el código de barras?")) {
      System.out.println("Código de barras actual: " + 
          (productoEditar.getCodigoBarras() != null ? 
          productoEditar.getCodigoBarras().toString() : "Sin código asignado"));
      
      System.out.println("Opciones:");
      System.out.println("1) Asignar nuevo código de barras");
      System.out.println("2) Quitar código de barras actual");
      System.out.println("3) Mantener actual");
      
      Integer opcion = InputValidator.leerIntegerSeguro(scanner, "Seleccione opción (1-3):");
      
      switch (opcion) {
        case 1:
          CodigoBarras nuevoCodigo = seleccionarCodigoBarrasCompleto();
          if (nuevoCodigo != null) {
            productoEditar.setCodigoBarras(nuevoCodigo);
            System.out.println("✅ Código de barras asignado correctamente");
          }
          break;
          
        case 2:
          productoEditar.setCodigoBarras(null);
          System.out.println("✅ Código de barras removido del producto");
          break;
          
        case 3:
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
    Long idProducto = InputValidator.leerLongSeguro(scanner, "Ingrese el ID del producto a buscar:");
  
    Producto productoEncontrado = buscaValidaProducto(idProducto);
    if (productoEncontrado != null) {
      System.out.println("✅ Producto encontrado: " + productoEncontrado);
      return;
    }
  }

  public void mostrarProductos() {
    System.out.println("--Lista de productos--");
    List<Producto> productos = (List<Producto>) productoService.getAll();
    if (productos.isEmpty()) {
      System.out.println("❌ No hay productos registrados.");
      return;
    }
    for (Producto p : productos) {
        System.out.println(p);
    }
  }

  public void mostrarProductosCategoria() {
    System.out.println("--Productos por Categoría--");
    String categoria = InputValidator.leerStringMayusculas(scanner, "Ingrese la categoría a filtrar:");
  
    if (categoria.isEmpty()) {
      System.out.println("❌ Categoría inválida. Operación cancelada.");
      return;
    }

    List<Producto> productos = (List<Producto>) productoService.buscarPorCategoria(categoria);
    
    if (productos.isEmpty()) {
      System.out.println("❌ No hay productos registrados para la categoría: " + categoria);
      return;
    }

    System.out.println("Productos encontrados:");
    for (Producto p : productos) {
      System.out.println(p);
    }
  }

  public void eliminarProducto() {
    System.out.println("--Eliminación de producto--");
    Long idProducto = InputValidator.leerLongSeguro(scanner, "Ingrese el ID del producto a eliminar:");
  
    Producto productoEliminar = buscaValidaProducto(idProducto);
    if (productoEliminar == null) return;
    
    System.out.println("Producto a eliminar: " + productoEliminar);
    
    if (InputValidator.leerConfirmacion(scanner, "¿Está seguro de que desea eliminar este producto?")) {
      Producto eliminado = productoService.eliminar(idProducto);
      if (eliminado != null) {
        System.out.println("✅ Producto eliminado correctamente: " + eliminado);
      } else {
        System.out.println("❌ Error al eliminar el producto.");
      }
    } else {
      System.out.println("Operación cancelada.");
    }
  }

  public void asignarCodigo() {
    System.out.println("--Asignación de código de barras a producto--");
    Long idProducto = InputValidator.leerLongSeguro(scanner, "Ingrese el ID del producto:");
  
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