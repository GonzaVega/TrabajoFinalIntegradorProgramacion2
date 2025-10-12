//CodigoBarrasController.java
package com.main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import com.entities.CodigoBarras;
import com.entities.TipoCodigoBarras;
import com.servicios.CodigoBarrasService;

public class CodigoBarrasController {
  private CodigoBarrasService codigoBarrasService;
  private Scanner scanner;

  public CodigoBarrasController() {
    codigoBarrasService = new CodigoBarrasService();
    scanner = new Scanner(System.in);
  }

  private CodigoBarras buscaValidaCodigoBarras(long idCodigoBarras) {
    CodigoBarras codigoBarras = codigoBarrasService.getById(idCodigoBarras);
    if (codigoBarras == null) System.out.println("❌ No se encontró un código de barras con el ID proporcionado.");
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
    System.out.println("Valor:");
    String valor = scanner.nextLine();
    System.out.println("Observaciones:");
    String observaciones = scanner.nextLine();
    
    mostrarTiposCodigoBarras();
    TipoCodigoBarras tipoSeleccionado = seleccionarTipoCodigoBarras();
    if (tipoSeleccionado == null) {
        System.out.println("❌ Opción inválida. Operación cancelada.");
        return;
    }
    
    CodigoBarras nuevoCodigoBarras = new CodigoBarras();
    nuevoCodigoBarras.setValor(valor);
    nuevoCodigoBarras.setObservaciones(observaciones);
    nuevoCodigoBarras.setFechaAsignacion(LocalDate.now());
    nuevoCodigoBarras.setTipo(tipoSeleccionado);
    
    CodigoBarras creado = codigoBarrasService.insertar(nuevoCodigoBarras);
    if (creado != null) {
      System.out.println("✅ Se creó correctamente su código de barras: " + creado);
      return;
    }
  }
  
  public void editarCodigoBarras() {
    System.out.println("--Edición de código de barras--");
    System.out.println("Por favor, ingrese el ID del código de barras a editar:");
    long idCodigoBarras = Long.parseLong(scanner.nextLine());
    
    CodigoBarras codigoEditar = buscaValidaCodigoBarras(idCodigoBarras);
    if (codigoEditar == null) return;
    
    System.out.println("Código de barras encontrado: " + codigoEditar);
    System.out.println("Ingrese los nuevos datos del código de barras (deje en blanco para mantener el valor actual):");
    
    System.out.println("Valor (" + codigoEditar.getValor() + "):");
    String valor = scanner.nextLine();
    if (!valor.isEmpty()) codigoEditar.setValor(valor);
    
    System.out.println("Observaciones (" + codigoEditar.getObservaciones() + "):");
    String observaciones = scanner.nextLine();
    if (!observaciones.isEmpty()) codigoEditar.setObservaciones(observaciones);
    
    System.out.println("¿Desea cambiar el tipo de código de barras? (S/N):");
    String cambiarTipo = scanner.nextLine().toUpperCase().trim();
    if (cambiarTipo.equals("S")) {
        mostrarTiposCodigoBarras();
        TipoCodigoBarras nuevoTipo = seleccionarTipoCodigoBarras();
        if (nuevoTipo != null) codigoEditar.setTipo(nuevoTipo);
    }
    
    CodigoBarras editado = codigoBarrasService.actualizar(codigoEditar);
    if (editado != null) {
      System.out.println("✅ Código de barras actualizado correctamente: " + editado);
      return;
    }
  }

  public void buscarCodigoBarras() {
    System.out.println("--Búsqueda de código de barras--");
    System.out.println("Por favor, ingrese el ID del código de barras a buscar:");
    long idCodigoBarras = Long.parseLong(scanner.nextLine());
    
    CodigoBarras codigoEncontrado = buscaValidaCodigoBarras(idCodigoBarras);
    if (codigoEncontrado != null) {
        System.out.println("✅ Código de barras encontrado: " + codigoEncontrado);
        return;
    }
  }

  public void mostrarCodigosBarras() {
    System.out.println("--Lista de Códigos de Barras--");
    ArrayList<CodigoBarras> codigos = (ArrayList<CodigoBarras>) codigoBarrasService.getAll();
    if (codigos.isEmpty()) System.out.println("❌ No hay códigos registrados.");
        
    for (CodigoBarras c : codigos) {
        System.out.println(c);
    }
  }

  //TODO: habilitar este método cuando este el DAO.
  // public void mostrarCodigosBarrasPorTipo() {
  //   System.out.println("--Códigos de Barras por Tipo--");
  //   mostrarTiposCodigoBarras();
  //   TipoCodigoBarras tipoSeleccionado = seleccionarTipoCodigoBarras();
  //   if (tipoSeleccionado == null) {
  //       System.out.println("❌ Opción inválida. Operación cancelada.");
  //       return;
  //   }
    
  //   ArrayList<CodigoBarras> codigos = (ArrayList<CodigoBarras>) codigoBarrasService.buscarPorTipo(tipoSeleccionado);
  //   if (codigos.isEmpty()) System.out.println("❌ No hay códigos registrados para el tipo seleccionado.");
        
  //   for (CodigoBarras c : codigos) {
  //       System.out.println(c);
  //   }
  // }

  public void eliminarCodigoBarras() {
    System.out.println("--Eliminación de código de barras--");
    System.out.println("Por favor, ingrese el ID del código de barras a eliminar:");
    long idCodigoBarras = Long.parseLong(scanner.nextLine());
    
    CodigoBarras codigoEliminar = buscaValidaCodigoBarras(idCodigoBarras);
    if (codigoEliminar == null) return;
    
    CodigoBarras eliminado = codigoBarrasService.eliminar(idCodigoBarras);
    if (eliminado != null) {
      System.out.println("✅ Código de barras eliminado correctamente: " + eliminado);
      return;
    }
  }
}