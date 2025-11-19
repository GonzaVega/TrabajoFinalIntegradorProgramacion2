# Trabajo Final Integrador Programación 2

## Integrantes del Proyecto
- **Eric Suárez Dubs - Comisión 13**
- **Mateo Serafini - Comisión 5**
- **Federico Iacono - Comisión 6**
- **Gonzalo Vega - Comisión 11**

## Información Académica
- **Carrera:** Tecnicatura Universitaria en Programación a Distancia
- **Materia:** Programación 2
- **Año:** 2025
- **Profesor Coordinador:** Carlos Martinez

## Video Demostrativo
🔗 [Ver video explicativo en YouTube](https://www.youtube.com/watch?v=1gK8EBLzP08)

---

## Descripción del Proyecto

Este es el repositorio correspondiente al Trabajo Final Integrador de la materia Programación 2. Se trata de una aplicación de escritorio en Java para la gestión de **Productos** y **Códigos de Barras**, implementando una arquitectura por capas con persistencia en MySQL.

---

## Guía de Configuración: Base de Datos MySQL (XAMPP) y Conexión Java

Esta guía detalla los pasos necesarios para configurar el servidor de base de datos MySQL usando **XAMPP**, cargar el *schema* y establecer la conexión en la aplicación Java a través del archivo `db.properties`.

---

### Paso 1: Iniciar el Servidor de Base de Datos

Asegúrate de que tu servidor MySQL/MariaDB esté en funcionamiento.

1. **Abrir el Panel de Control de XAMPP.**
2. **Iniciar el Módulo MySQL:** Haz clic en el botón **"Start"** junto al módulo `MySQL`.

> ℹ️ Una vez iniciado, el servidor estará escuchando, generalmente en el puerto **`3306`**.

---

### Paso 2: Cargar tu Schema en la Base de Datos

Necesitas crear la base de datos y sus tablas utilizando el script SQL preparado.

1. **Acceder a phpMyAdmin:** En el panel de XAMPP, haz clic en el botón **"Admin"** que se encuentra junto a MySQL. Se abrirá la herramienta web en tu navegador.
2. **Seleccionar la pestaña "SQL":** En la interfaz de phpMyAdmin, localiza y haz clic en la pestaña **"SQL"** en la parte superior.
3. **Copiar y Pegar los Scripts:**
   * Abre los archivos **`schemaTpiProductoCodigoDeBarras.sql`** y **`scriptParaGenerarDatosMasivos.sql`** en un editor de texto (VS Code, Notepad++, etc.).
   * Copia **todo el contenido** de los archivos.
   * Pega el contenido en el gran cuadro de texto de la pestaña SQL de phpMyAdmin.
4. **Ejecutar el Script:** Haz clic en el botón **"Continuar"** o **"Go"** (usualmente en la esquina inferior derecha).

> **Verificación:** Si la ejecución es exitosa, un mensaje de éxito aparecerá y tu **nueva base de datos** (con las tablas `productos` y `codigos_barras`) se listará en la columna de la izquierda.

---

### Paso 3: Configurar tu Archivo `db.properties`

Este paso asegura que tu aplicación Java tenga las credenciales correctas para conectarse a la base de datos recién creada.

1. **Localizar el archivo de ejemplo:** En tu proyecto, navega hasta **`src/main/resources/`** y localiza el archivo **`db.properties.example`**.

2. **Crear tu archivo de configuración:**
     - **Renómbralo** a `db.properties` (sin el `.example`).

3. **Editar las credenciales:** Abre el archivo **`db.properties`** recién creado y verifica/actualiza el contenido con la siguiente configuración estándar de XAMPP o el servidor MySQL de tu preferencia:

```properties
# Contenido de src/main/resources/db.properties

db.url=jdbc:mysql://localhost:3306/tpi_productos

# Por defecto, el usuario de XAMPP es 'root'
db.user=root

# Por defecto, la contraseña de XAMPP está vacía
db.password=

# El driver JDBC para MySQL
db.driver=com.mysql.cj.jdbc.Driver
```

---

## Paso 4: Ejecutar el archivo `src/main/java/com/main/App.java`

Este paso ejecuta la aplicación Java.

**Elegir:**
1. **Modo Consola**: Interfaz por línea de comandos.
2. **Modo Gráfico**: Interfaz gráfica con Swing.

---

## Arquitectura del Proyecto

- **Entities (`com.entities`):** Clases POJO (Producto, CodigoBarras, TipoCodigoBarras).
- **DAO (`com.dao`):** Capa de acceso a datos con JDBC.
- **Services (`com.servicios`):** Lógica de negocio y control de transacciones.
- **Controllers (`com.main`):** Controladores de presentación.
- **Utils (`com.utils`):** Validadores de entrada y manejo centralizado de excepciones.
- **Config (`com.config`):** Gestión de conexión a base de datos.

---

## Tecnologías Utilizadas

- **Lenguaje:** Java 21
- **Build Tool:** Maven
- **Base de Datos:** MySQL 8.0
- **JDBC Driver:** MySQL Connector/J
- **IDE Recomendado:** NetBeans / IntelliJ IDEA
