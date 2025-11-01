//DatabaseConnection.java
package com.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static Properties properties = new Properties();

    // Bloque estático para cargar propiedades una sola vez
    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("Lo siento, no se pudo encontrar el archivo db.properties");
            } else {
                properties.load(input);
                Class.forName(properties.getProperty("db.driver"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar la configuración de la base de datos.", e);
        }
    }

    /**
     * Constructor privado para evitar que se instancie esta clase de utilidad.
     */
    private DatabaseConnection() {
    }

    /**
     * Obtiene una nueva conexión a la base de datos.
     * El que llama a este método es responsable de cerrar la conexión.
     * @return un objeto Connection a la base de datos.
     * @throws SQLException si ocurre un error de acceso a la base de datos.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
        );
    }
}