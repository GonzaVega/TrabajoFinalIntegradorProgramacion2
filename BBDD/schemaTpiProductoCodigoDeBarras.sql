-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS tpi_productos;
USE tpi_productos;

-- Crear tabla CodigoBarras (SIN la referencia al producto)
CREATE TABLE codigos_barras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    tipo ENUM('EAN13','EAN8','UPC') NOT NULL,
    valor VARCHAR(20) NOT NULL UNIQUE,
    fecha_asignacion DATE,
    observaciones VARCHAR(255)
    -- Se elimina la columna producto_id de aquí
);

-- Crear tabla Producto (CON la referencia al código de barras)
CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nombre VARCHAR(120) NOT NULL,
    marca VARCHAR(80),
    categoria VARCHAR(80),
    precio DECIMAL(10,2) NOT NULL,
    peso DECIMAL(10,3),
    codigos_barras_id BIGINT UNIQUE, -- Se agrega la columna aquí. UNIQUE garantiza la relación 1 a 1
    CONSTRAINT fk_producto_codigobarras
        FOREIGN KEY (codigos_barras_id)
        REFERENCES codigos_barras(id)
        ON DELETE SET NULL -- Si se borra un código, el producto queda sin código, pero no se borra.
);

-- Mejorar performance
CREATE INDEX idx_producto_eliminado ON productos(eliminado);
CREATE INDEX idx_producto_nombre ON productos(nombre);
CREATE INDEX idx_codigobarras_eliminado ON codigos_barras(eliminado);
CREATE INDEX idx_codigobarras_valor ON codigos_barras(valor);