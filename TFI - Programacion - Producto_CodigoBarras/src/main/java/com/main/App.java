// App.java
package com.main;

import com.config.DatabaseConnection;
import com.dao.CodigoBarrasDaoImpl;
import com.entities.CodigoBarras;
import com.entities.TipoCodigoBarras;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import com.dao.GenericDao;

public class App {

    public static void main(String[] args) {
        System.out.println("--- Iniciando prueba de conexión y DAO ---");

        // Usamos try-with-resources para asegurar que la conexión se cierre sola
        try (Connection connection = DatabaseConnection.getConnection()) {

            System.out.println("✅ Conexión a la base de datos exitosa.");

            // 1. Instanciamos el DAO que queremos probar
            GenericDao<CodigoBarras> codigoBarrasDao = new CodigoBarrasDaoImpl();

            // 2. Creamos un objeto de prueba
            CodigoBarras nuevoCodigo = new CodigoBarras();
            nuevoCodigo.setTipo(TipoCodigoBarras.EAN13);
            nuevoCodigo.setValor("1234567890123");
            nuevoCodigo.setFechaAsignacion(LocalDate.now());
            nuevoCodigo.setObservaciones("Este es un objeto de prueba.");

            System.out.println("Intentando guardar: " + nuevoCodigo);

            // 3. Usamos el DAO para guardar el objeto en la BD
            CodigoBarras codigoGuardado = codigoBarrasDao.crear(nuevoCodigo, connection);

            // 4. Verificamos el resultado
            System.out.println("🎉 ¡Guardado exitosamente en la base de datos!");
            System.out.println("Objeto guardado (devuelto por el DAO): " + codigoGuardado);

            // La prueba real es ver si el ID fue asignado:
            if (codigoGuardado.getId() != null && codigoGuardado.getId() > 0) {
                System.out.println("✅ PRUEBA SUPERADA: El ID fue asignado por la base de datos: " + codigoGuardado.getId());
            } else {
                System.out.println("❌ PRUEBA FALLIDA: El ID no fue asignado.");
            }

        } catch (SQLException e) {
            System.err.println("❌ ERROR: Ocurrió un problema con la base de datos.");
            e.printStackTrace(); // Imprime el detalle del error para depurar
        } catch (Exception e) {
            System.err.println("❌ ERROR: Ocurrió un error inesperado.");
            e.printStackTrace();
        }

        System.out.println("--- Fin de la prueba ---");
    }
}