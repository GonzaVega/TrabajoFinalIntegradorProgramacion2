package com.main;

import java.util.Scanner;
import com.utils.InputValidator;

public class AppMenu {
    public static void start() {
        System.out.println("Aplicación iniciada desde AppLauncher.");
        Scanner scanner = new Scanner(System.in);
        ProductoController productoController = new ProductoController();
        CodigoBarrasController codigoBarrasController = new CodigoBarrasController();
        boolean salir = false;

        while (!salir) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Crear nuevo producto");
            System.out.println("2. Editar producto existente");
            System.out.println("3. Crear nuevo código de barras");
            System.out.println("4. Editar código de barras existente");
            System.out.println("5. Buscar producto");
            System.out.println("6. Buscar código de barras");
            System.out.println("7. Asignar código de barras a producto");
            System.out.println("8. Eliminar producto");
            System.out.println("9. Eliminar código de barras");
            System.out.println("10. Listar productos");
            System.out.println("11. Listar códigos de barras");
            System.out.println("12. Buscar por categoria de producto");
            System.out.println("13. Buscar por tipo de códigos de barras");
            System.out.println("0. Salir");
            Integer opcionUsuario = InputValidator.leerIntegerSeguro(scanner, "\nSeleccione una opción:");

            switch (opcionUsuario) {
                case 1:
                    productoController.crearProducto();
                    break;
                case 2:
                    productoController.editarProducto();
                    break;
                case 3:
                    codigoBarrasController.crearCodigoBarras();
                  break;
                case 4:
                    codigoBarrasController.editarCodigoBarras();
                  break;
                case 5:
                    productoController.buscarProducto();
                  break;
                case 6:
                    codigoBarrasController.buscarCodigoBarras();
                  break;
                case 7:
                    productoController.asignarCodigo();
                  break;
                case 8:
                    productoController.eliminarProducto();
                  break;
                case 9:
                    codigoBarrasController.eliminarCodigoBarras();
                  break;
                case 10:
                    productoController.mostrarProductos();
                  break;
                case 11:
                    codigoBarrasController.mostrarCodigosBarras();
                  break;
                case 12:
                    productoController.mostrarProductosCategoria();
                  break;
                case 13:
                    codigoBarrasController.mostrarCodigosBarrasPorTipo();
                  break;
                case 0:
                    if (InputValidator.leerConfirmacion(scanner, "¿Está seguro de que desea salir?")) {
                      System.out.println("Saliendo del sistema...");
                      salir = true;
                    }
                  break;
                default:
                  System.out.println(" Opción inválida. Por favor seleccione una opción del 0 al 13.");
                  break;
              }
        }
        scanner.close();
        System.out.println("Aplicación finalizada.");
    }
  }
