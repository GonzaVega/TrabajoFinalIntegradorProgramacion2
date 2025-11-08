// CodigoBarrasContoller.java
package com.main;

import java.time.LocalDate;
import java.util.List; // <-- CORRECCIÓN: Usar List en lugar de ArrayList
import java.util.Scanner;

import com.entities.CodigoBarras;
import com.entities.TipoCodigoBarras;
import com.servicios.CodigoBarrasService;
import com.utils.InputValidator;

/**
 * Controlador para las operaciones de CodigoBarras en modo consola. Orquesta la
 * entrada del usuario y llama a la capa de servicio.
 */
public class CodigoBarrasController {

    private CodigoBarrasService codigoBarrasService;
    private Scanner scanner;

    public CodigoBarrasController() {
        codigoBarrasService = new CodigoBarrasService();
        scanner = new Scanner(System.in);
    }

    /**
     * Busca un código de barras por ID y valida si existe.
     *
     * @param idCodigoBarras El ID a buscar.
     * @return El código de barras si se encuentra, o null.
     */
    private CodigoBarras buscaValidaCodigoBarras(long idCodigoBarras) {
        CodigoBarras codigoBarras = codigoBarrasService.getById(idCodigoBarras);
        if (codigoBarras == null) {
            System.out.println("❌ No se encontró un código de barras con el ID proporcionado.");
        }
        return codigoBarras;
    }

    /**
     * Muestra las opciones de TipoCodigoBarras al usuario.
     */
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

    /**
     * Lee la selección del usuario para un TipoCodigoBarras.
     *
     * @return El TipoCodigoBarras seleccionado, o null si es inválido.
     */
    public TipoCodigoBarras seleccionarTipoCodigoBarras() {
        String opcion = scanner.nextLine().toUpperCase().trim();

        if (opcion.length() != 1) {
            return null;
        }

        char letra = opcion.charAt(0);
        TipoCodigoBarras[] tipos = TipoCodigoBarras.values();
        int indice = letra - 'A';

        if (indice >= 0 && indice < tipos.length) {
            return tipos[indice];
        }

        return null;
    }

    /**
     * Flujo para crear un nuevo código de barras.
     */
    public void crearCodigoBarras() {
        try {
            System.out.println("--Creación de nuevo código de barras--");
            System.out.println("Por favor, ingrese los datos del código de barras:");
            String observaciones = InputValidator.leerString(scanner, "Observaciones:");

            mostrarTiposCodigoBarras();
            TipoCodigoBarras tipoSeleccionado = seleccionarTipoCodigoBarras();
            if (tipoSeleccionado == null) {
                System.out.println("❌ Opción inválida. Operación cancelada.");
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

            CodigoBarras creado = codigoBarrasService.insertar(nuevoCodigoBarras);
            if (creado != null) {
                System.out.println("✅ Se creó correctamente su código de barras: " + creado);
                return;
            }
        } catch (RuntimeException e) {
            System.out.println("❌ " + e.getMessage());
            System.out.println("   Creación de código cancelada. Volviendo al menú principal.");
        }
    }

    /**
     * Flujo para editar un código de barras existente.
     */
    public void editarCodigoBarras() {
        try {
            System.out.println("--Edición de código de barras--");
            Long idCodigoBarras = InputValidator.leerLongSeguro(scanner, "Ingrese el ID del código de barras a editar:");

            CodigoBarras codigoEditar = buscaValidaCodigoBarras(idCodigoBarras);
            if (codigoEditar == null) {
                return;
            }

            System.out.println("Código de barras encontrado: " + codigoEditar);
            System.out.println("Ingrese los nuevos datos del código de barras (deje en blanco para mantener el valor actual):");

            // El valor no se debe editar si no se cambia el tipo, 
            // para eso se usa la lógica de validación de formato
            String observaciones = InputValidator.leerStringOpcional(scanner, "Nuevas observaciones:", codigoEditar.getObservaciones());
            codigoEditar.setObservaciones(observaciones);

            if (InputValidator.leerConfirmacion(scanner, "¿Desea cambiar el tipo de código de barras? (esto requerirá un nuevo valor)")) {
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
                System.out.println("✅ Código de barras actualizado correctamente: " + editado);
                return;
            }
        } catch (RuntimeException e) {
            System.out.println("❌ " + e.getMessage());
            System.out.println("   Edición de código cancelada. Volviendo al menú principal.");
        }
    }

    /**
     * Flujo para buscar un código de barras por ID.
     */
    public void buscarCodigoBarras() {
        try {
            System.out.println("--Búsqueda de código de barras--");
            Long idCodigoBarras = InputValidator.leerLongSeguro(scanner, "Ingrese el ID del código de barras a buscar:");

            CodigoBarras codigoEncontrado = buscaValidaCodigoBarras(idCodigoBarras);
            if (codigoEncontrado != null) {
                System.out.println("✅ Código de barras encontrado: " + codigoEncontrado);
                return;
            }
        } catch (RuntimeException e) {
            System.out.println("❌ " + e.getMessage());
            System.out.println("   Búsqueda cancelada. Volviendo al menú principal.");
        }
    }

    /**
     * Muestra todos los códigos de barras no eliminados.
     */
    public void mostrarCodigosBarras() {
        System.out.println("--Lista de Códigos de Barras--");
        List<CodigoBarras> codigos = codigoBarrasService.getAll(); // <-- CORRECCIÓN: Usar List
        if (codigos.isEmpty()) {
            System.out.println("❌ No hay códigos registrados.");
            return;
        }

        for (CodigoBarras c : codigos) {
            System.out.println(c);
        }
    }

    /**
     * Muestra códigos de barras filtrados por tipo.
     */
    public void mostrarCodigosBarrasPorTipo() {
        try {
            System.out.println("--Códigos de Barras por Tipo--");
            mostrarTiposCodigoBarras();
            TipoCodigoBarras tipoSeleccionado = seleccionarTipoCodigoBarras();
            if (tipoSeleccionado == null) {
                System.out.println("❌ Opción inválida. Operación cancelada.");
                return;
            }

            List<CodigoBarras> codigos = codigoBarrasService.buscarPorTipo(tipoSeleccionado); // <-- CORRECCIÓN: Usar List
            if (codigos.isEmpty()) {
                System.out.println("❌ No hay códigos registrados para el tipo seleccionado.");
                return;
            }

            for (CodigoBarras c : codigos) {
                System.out.println(c);
            }
        } catch (RuntimeException e) {
            System.out.println("❌ " + e.getMessage());
            System.out.println("   Búsqueda cancelada. Volviendo al menú principal.");
        }
    }

    /**
     * Flujo para eliminar lógicamente un código de barras.
     */
    public void eliminarCodigoBarras() {
        try {
            System.out.println("--Eliminación de código de barras--");
            Long idCodigoBarras = InputValidator.leerLongSeguro(scanner, "Ingrese el ID del código de barras a eliminar:");

            CodigoBarras codigoEliminar = buscaValidaCodigoBarras(idCodigoBarras);
            if (codigoEliminar == null) {
                return;
            }

            System.out.println("Código de barras a eliminar: " + codigoEliminar);

            if (InputValidator.leerConfirmacion(scanner, "¿Está seguro de que desea eliminar este código de barras?")) {
                CodigoBarras eliminado = codigoBarrasService.eliminar(idCodigoBarras);
                if (eliminado != null) {
                    System.out.println("✅ Código de barras eliminado correctamente: " + eliminado);
                } else {
                    System.out.println("❌ Error al eliminar el código de barras.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (RuntimeException e) {
            System.out.println("❌ " + e.getMessage());
            System.out.println("   Eliminación cancelada. Volviendo al menú principal.");
        }
    }
}
