//DatabaseConnection.java
package com.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase de utilidad (utility class) para gestionar la conexión a la base de
 * datos. Carga la configuración desde un archivo .properties externo.
 */
public class DatabaseConnection {

    private static Properties properties = new Properties();

    /**
     * Bloque estático que se ejecuta una sola vez al cargar la clase. Lee el
     * archivo db.properties desde el classpath, carga las propiedades y
     * registra el driver JDBC de MySQL.
     */
    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("Lo siento, no se pudo encontrar el archivo db.properties");
                throw new RuntimeException("Archivo db.properties no encontrado en el classpath.");
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
     * Constructor privado para evitar que esta clase de utilidad sea
     * instanciada.
     */
    private DatabaseConnection() {
    }

    /**
     * Obtiene una nueva conexión a la base de datos utilizando las propiedades
     * cargadas. El método que llama a esta función es responsable de cerrar la
     * conexión.
     *
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
