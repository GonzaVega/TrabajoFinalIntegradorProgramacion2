package com.main;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import com.entities.CodigoBarras;
import com.entities.TipoCodigoBarras;
import com.servicios.CodigoBarrasService;
import com.utils.InputValidator;

public class CodigoBarrasController {
  private CodigoBarrasService codigoBarrasService;
  private Scanner scanner;

  public CodigoBarrasController() {
    codigoBarrasService = new CodigoBarrasService();
    scanner = new Scanner(System.in);
  }

  private CodigoBarras buscaValidaCodigoBarras(long idCodigoBarras) {
    CodigoBarras codigoBarras = codigoBarrasService.getById(idCodigoBarras);
    if (codigoBarras == null) {
      System.out.println(" No se encontró un código de barras con el ID proporcionado.");
    }
    return codigoBarras;
  }

  public void mostrarTiposCodigoBarras() {
    System.out.println("Seleccione el tipo de código de barras:");
    TipoCodigoBarras[] tipos = TipoCodigoBarras.values();
    
    char opcion = 'A';
    for (TipoCodigoBarras tipo : tipos) {
        System.out.println(opcion + ") " + tipo.name());
        opcion++;
    }
    System.out.println("Ingrese su opción (A, B, C, etc.):");
  }

  public TipoCodigoBarras seleccionarTipoCodigoBarras() {
    String opcion = scanner.nextLine().toUpperCase().trim();
    
    if (opcion.length() != 1) return null;
    
    char letra = opcion.charAt(0);
    TipoCodigoBarras[] tipos = TipoCodigoBarras.values();
    int indice = letra - 'A';
    
    if (indice >= 0 && indice < tipos.length) return tipos[indice];
    
    return null;
  }

  public void crearCodigoBarras() {

    System.out.println("--Creación de nuevo código de barras--");
    System.out.println("Por favor, ingrese los datos del código de barras:");
    String observaciones = InputValidator.leerString(scanner, "Observaciones:");

    mostrarTiposCodigoBarras();
    TipoCodigoBarras tipoSeleccionado = seleccionarTipoCodigoBarras();
    if (tipoSeleccionado == null) {
      System.out.println(" Opción inválida. Operación cancelada.");
      return;
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
    

    CodigoBarras nuevoCodigoBarras = new CodigoBarras();
    nuevoCodigoBarras.setValor(valor);
    nuevoCodigoBarras.setObservaciones(observaciones);
    nuevoCodigoBarras.setFechaAsignacion(LocalDate.now());
    nuevoCodigoBarras.setTipo(tipoSeleccionado);
    System.out.println("5");
    
    CodigoBarras creado = codigoBarrasService.insertar(nuevoCodigoBarras);

    if (creado != null) {
      System.out.println("7");
      System.out.println(" Se creó correctamente su código de barras: " + creado);
      return;
    }
  }
  
  public void editarCodigoBarras() {
    System.out.println("--Edición de código de barras--");
    Long idCodigoBarras = InputValidator.leerLongSeguro(scanner, "Ingrese el ID del código de barras a editar:");
  
    CodigoBarras codigoEditar = buscaValidaCodigoBarras(idCodigoBarras);
    if (codigoEditar == null) return;
    
    System.out.println("Código de barras encontrado: " + codigoEditar);
    System.out.println("Ingrese los nuevos datos del código de barras (deje en blanco para mantener el valor actual):");
    
    String valor = InputValidator.leerString(scanner, "Valor [" + codigoEditar.getValor() + "]:");
    if (!valor.isEmpty()) codigoEditar.setValor(valor);
    
    String observaciones = InputValidator.leerStringOpcional(scanner, "Nuevas observaciones:", codigoEditar.getObservaciones());
    codigoEditar.setObservaciones(observaciones);
    
    if (InputValidator.leerConfirmacion(scanner, "¿Desea cambiar el tipo de código de barras?")) {
      mostrarTiposCodigoBarras();
      TipoCodigoBarras nuevoTipo = seleccionarTipoCodigoBarras();
      if (nuevoTipo != null) {
        codigoEditar.setTipo(nuevoTipo);
        String nuevoValor;
        switch (nuevoTipo) {
          case EAN13:
            nuevoValor = InputValidator.leerCodigoBarras(scanner, "Nuevo valor (13 dígitos):", 13);
            break;
          case EAN8:
            nuevoValor = InputValidator.leerCodigoBarras(scanner, "Nuevo valor (8 dígitos):", 8);
            break;
          case UPC:
            nuevoValor = InputValidator.leerCodigoBarras(scanner, "Nuevo valor (12 dígitos):", 12);
            break;
          default:
            nuevoValor = InputValidator.leerStringNoVacio(scanner, "Nuevo valor:");
            break;
        }
        codigoEditar.setValor(nuevoValor);
      } else {
        System.out.println("Tipo de código mantenido sin cambios.");
      }
    }

    CodigoBarras editado = codigoBarrasService.actualizar(codigoEditar);
    if (editado != null) {
      System.out.println(" Código de barras actualizado correctamente: " + editado);
      return;
    }
  }

  public void buscarCodigoBarras() {
    System.out.println("--Búsqueda de código de barras--");
    Long idCodigoBarras = InputValidator.leerLongSeguro(scanner, "Ingrese el ID del código de barras a buscar:");
  
    CodigoBarras codigoEncontrado = buscaValidaCodigoBarras(idCodigoBarras);
    if (codigoEncontrado != null) {
      System.out.println(" Código de barras encontrado: " + codigoEncontrado);
      return;
    }
  }

  public void mostrarCodigosBarras() {
    System.out.println("--Lista de Códigos de Barras--");
    List<CodigoBarras> codigos = (List<CodigoBarras>) codigoBarrasService.getAll();
    if (codigos.isEmpty()) {
      System.out.println(" No hay códigos registrados.");
      return;
    }
    
    for (CodigoBarras c : codigos) {
        System.out.println(c);
    }
  }

  public void mostrarCodigosBarrasPorTipo() {
    System.out.println("--Códigos de Barras por Tipo--");
    mostrarTiposCodigoBarras();
    TipoCodigoBarras tipoSeleccionado = seleccionarTipoCodigoBarras();
    if (tipoSeleccionado == null) {
      System.out.println(" Opción inválida. Operación cancelada.");
      return;
    }

    List<CodigoBarras> codigos = (List<CodigoBarras>) codigoBarrasService.buscarPorTipo(tipoSeleccionado);
    if (codigos.isEmpty()) {
      System.out.println(" No hay códigos registrados para el tipo seleccionado.");
      return;
    }
    
    for (CodigoBarras c : codigos) {
      System.out.println(c);
    }
  }

  public void eliminarCodigoBarras() {
    System.out.println("--Eliminación de código de barras--");
    Long idCodigoBarras = InputValidator.leerLongSeguro(scanner, "Ingrese el ID del código de barras a eliminar:");
  
    CodigoBarras codigoEliminar = buscaValidaCodigoBarras(idCodigoBarras);
    if (codigoEliminar == null) return;
    
    System.out.println("Código de barras a eliminar: " + codigoEliminar);
    
    if (InputValidator.leerConfirmacion(scanner, "¿Está seguro de que desea eliminar este código de barras?")) {
      CodigoBarras eliminado = codigoBarrasService.eliminar(idCodigoBarras);
      if (eliminado != null) {
        System.out.println(" Código de barras eliminado correctamente: " + eliminado);
      } else {
        System.out.println(" Error al eliminar el código de barras.");
      }
    } else {
      System.out.println("Operación cancelada.");
    }
  }
}