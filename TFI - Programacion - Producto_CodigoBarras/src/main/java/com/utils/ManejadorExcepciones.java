// ManejadorExcepciones.java
package com.utils;

import java.sql.SQLException;

public class ManejadorExcepciones {
  public static void manejarEntradaInvalida(EntradaInvalidaException e) {
      System.out.println("❌ ERROR DE ENTRADA:");
      System.out.println("   " + e.getMessage());
  }
    
  public static void manejarErrorBaseDatos(SQLException e, String operacion) {
    System.out.println("❌ ERROR DE BASE DE DATOS:");
    System.out.println("  → Operación: " + operacion);
    System.out.println("  → Código SQL: " + e.getErrorCode());
    System.out.println("  → Mensaje: " + e.getMessage());
        
    switch (e.getErrorCode()) {
      case 1062: 
        System.out.println("  → Causa probable: Violación de unicidad (registro duplicado)");
        break;
      case 1048: 
        System.out.println("  → Causa probable: Campo obligatorio sin valor");
        break;
      case 1452: 
        System.out.println("  → Causa probable: Violación de integridad referencial");
        break;
      case 0: 
        System.out.println("  → Causa probable: No se pudo conectar a la base de datos");
        break;
      }
  }
    
  public static void manejarErrorValidacion(String campo, String motivo) {
    System.out.println("❌ ERROR DE VALIDACIÓN:");
    System.out.println("  → Campo: " + campo);
    System.out.println("  → Motivo: " + motivo);
  }
}
