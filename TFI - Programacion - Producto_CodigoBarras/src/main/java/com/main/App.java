// App.java
package com.main;

import java.util.Scanner;

public class App {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    
    System.out.println("╔════════════════════════════════════════╗");
    System.out.println("║  SISTEMA DE GESTIÓN DE PRODUCTOS      ║");
    System.out.println("╚════════════════════════════════════════╝");
    System.out.println();
    System.out.println("Seleccione modo de ejecución:");
    System.out.println("  1. Modo Consola");
    System.out.println("  2. Modo Gráfico");
    System.out.print("\nOpción: ");
    
    try {
      int modo = sc.nextInt();
      
      if (modo == 2) {
        sc.close();
        javax.swing.SwingUtilities.invokeLater(() -> new AppMenuGUI());
      } else if (modo == 1) {
        AppMenu.start();
      } else {
        System.out.println("❌ Opción inválida");
        sc.close();
      }
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      sc.close();
    }
  }
}