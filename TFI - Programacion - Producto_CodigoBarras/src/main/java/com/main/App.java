// package com.main;

// public class App {
//     public static void main(String[] args) {
//         AppMenu.start();
//     }
// }

package com.main;

import java.util.Scanner;

public class App {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    
    System.out.println("╔════════════════════════════════════════╗");
    System.out.println("║     SISTEMA DE GESTIÓN DE PRODUCTOS    ║");
    System.out.println("╚════════════════════════════════════════╝");
    System.out.println();
    System.out.println("Seleccione modo de ejecución:");
    System.out.println("  1. Modo Consola");
    System.out.println("  2. Modo Gráfico");
    System.out.print("\nOpción: ");
    
    int modo = sc.nextInt();
    sc.close();
    
    if (modo == 2) {
      javax.swing.SwingUtilities.invokeLater(() -> new AppMenuGUI());
    } else {
      AppMenu.start();
    }
  }
}