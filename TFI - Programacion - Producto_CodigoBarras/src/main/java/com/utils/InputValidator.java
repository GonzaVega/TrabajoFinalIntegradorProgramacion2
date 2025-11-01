package com.utils;

import java.util.Scanner;

public class InputValidator {
	private static final int MAX_INTENTOS_DEFAULT = 3;

	public static Long leerLongSeguro(Scanner scanner, String mensaje) {
		return leerLongSeguro(scanner, mensaje, MAX_INTENTOS_DEFAULT);
	}

	public static Long leerLongSeguro(Scanner scanner, String mensaje, int maxIntentos) {
		int intentos = 0;

		while (intentos < maxIntentos) {
			try {
				System.out.println(mensaje);
				String input = scanner.nextLine().trim();

				if (input.isEmpty()) {
					throw new EntradaInvalidaException(
						"La entrada no puede estar vacía",
						input,
						"Número entero (ej: 123)"
					);
				}

				return Long.parseLong(input);

			} catch (NumberFormatException e) {
				intentos++;
				EntradaInvalidaException inputEx = new EntradaInvalidaException(
					"Debe ingresar un número entero válido",
					"entrada no numérica",
					"Número entero sin decimales (ej: 42)"
				);

				if (intentos < maxIntentos) {
					ManejadorExcepciones.manejarEntradaInvalida(inputEx);
					System.out.println("⚠️  Intentos restantes: " + (maxIntentos - intentos));
				} else {
					ManejadorExcepciones.manejarEntradaInvalida(inputEx);
					throw new RuntimeException("Máximo de intentos alcanzado para entrada numérica");
				}
			} catch (EntradaInvalidaException e) {
				intentos++;
				if (intentos < maxIntentos) {
					ManejadorExcepciones.manejarEntradaInvalida(e);
					System.out.println("⚠️  Intentos restantes: " + (maxIntentos - intentos));
				} else {
					ManejadorExcepciones.manejarEntradaInvalida(e);
					throw new RuntimeException("Máximo de intentos alcanzado");
				}
			}
		}

		throw new RuntimeException("No se pudo leer un número entero válido");
	}

	public static Double leerDoubleSeguro(Scanner scanner, String mensaje) {
		return leerDoubleSeguro(scanner, mensaje, MAX_INTENTOS_DEFAULT);
	}

	public static Double leerDoubleSeguro(Scanner scanner, String mensaje, int maxIntentos) {
		int intentos = 0;

		while (intentos < maxIntentos) {
			try {
				System.out.println(mensaje);
				String input = scanner.nextLine().trim();

				if (input.isEmpty()) {
					throw new EntradaInvalidaException(
						"La entrada no puede estar vacía",
						input,
						"Número decimal (ej: 123.45)"
					);
				}

				return Double.parseDouble(input);

			} catch (NumberFormatException e) {
				intentos++;
				EntradaInvalidaException inputEx = new EntradaInvalidaException(
					"Debe ingresar un número decimal válido",
					"entrada no numérica",
					"Número con punto como separador decimal (ej: 12.5)"
				);

				if (intentos < maxIntentos) {
					ManejadorExcepciones.manejarEntradaInvalida(inputEx);
					System.out.println("⚠️  Intentos restantes: " + (maxIntentos - intentos));
				} else {
					ManejadorExcepciones.manejarEntradaInvalida(inputEx);
					throw new RuntimeException("Máximo de intentos alcanzado para entrada decimal");
				}
			} catch (EntradaInvalidaException e) {
				intentos++;
				if (intentos < maxIntentos) {
					ManejadorExcepciones.manejarEntradaInvalida(e);
					System.out.println("⚠️  Intentos restantes: " + (maxIntentos - intentos));
				} else {
					ManejadorExcepciones.manejarEntradaInvalida(e);
					throw new RuntimeException("Máximo de intentos alcanzado");
				}
			}
		}

		throw new RuntimeException("No se pudo leer un número decimal válido");
	}

	public static Integer leerIntegerSeguro(Scanner scanner, String mensaje) {
		return leerIntegerSeguro(scanner, mensaje, MAX_INTENTOS_DEFAULT);
	}

	public static Integer leerIntegerSeguro(Scanner scanner, String mensaje, int maxIntentos) {
		int intentos = 0;

		while (intentos < maxIntentos) {
			try {
				System.out.println(mensaje);
				String input = scanner.nextLine().trim();

				if (input.isEmpty()) {
					throw new EntradaInvalidaException(
						"La entrada no puede estar vacía",
						input,
						"Número entero (ej: 5)"
					);
				}

				return Integer.parseInt(input);

			} catch (NumberFormatException e) {
				intentos++;
				EntradaInvalidaException inputEx = new EntradaInvalidaException(
					"Debe ingresar un número entero válido",
					"entrada no numérica",
					"Número entero (ej: 1, 2, 3)"
				);

				if (intentos < maxIntentos) {
					ManejadorExcepciones.manejarEntradaInvalida(inputEx);
					System.out.println("⚠️  Intentos restantes: " + (maxIntentos - intentos));
				} else {
					ManejadorExcepciones.manejarEntradaInvalida(inputEx);
					throw new RuntimeException("Máximo de intentos alcanzado");
				}
			} catch (EntradaInvalidaException e) {
				intentos++;
				if (intentos < maxIntentos) {
					ManejadorExcepciones.manejarEntradaInvalida(e);
					System.out.println("⚠️  Intentos restantes: " + (maxIntentos - intentos));
				} else {
					ManejadorExcepciones.manejarEntradaInvalida(e);
					throw new RuntimeException("Máximo de intentos alcanzado");
				}
			}
		}

		throw new RuntimeException("No se pudo leer un número entero válido");
	}

	public static String leerStringMayusculas(Scanner scanner, String mensaje) {
		System.out.println(mensaje);
		return scanner.nextLine().trim().toUpperCase();
	}

	public static String leerString(Scanner scanner, String mensaje) {
		System.out.println(mensaje);
		return scanner.nextLine().trim();
	}

	public static boolean leerConfirmacion(Scanner scanner, String mensaje) {
		int intentos = 0;

		while (intentos < MAX_INTENTOS_DEFAULT) {
			System.out.println(mensaje + " (S/N):");
			String input = scanner.nextLine().trim().toUpperCase();

			if (input.equals("S")) {
				return true;
			} else if (input.equals("N")) {
				return false;
			} else {
				intentos++;
				EntradaInvalidaException e = new EntradaInvalidaException(
					"Debe ingresar 'S' o 'N'",
					input,
					"'S' para Sí, 'N' para No (sin distinción de mayúsculas)"
				);

				if (intentos < MAX_INTENTOS_DEFAULT) {
					ManejadorExcepciones.manejarEntradaInvalida(e);
					System.out.println("⚠️  Intentos restantes: " + (MAX_INTENTOS_DEFAULT - intentos));
				} else {
					ManejadorExcepciones.manejarEntradaInvalida(e);
					throw new RuntimeException("Máximo de intentos alcanzado para confirmación");
				}
			}
		}

		throw new RuntimeException("No se pudo leer una confirmación válida");
	}
}
