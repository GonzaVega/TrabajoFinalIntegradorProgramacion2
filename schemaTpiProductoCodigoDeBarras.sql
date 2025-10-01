-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS tpi_productos;
USE tpi_productos;

-- Crear tabla Producto
CREATE TABLE producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nombre VARCHAR(120) NOT NULL,
    marca VARCHAR(80),
    categoria VARCHAR(80),
    precio DECIMAL(10,2) NOT NULL,
    peso DECIMAL(10,3)
);

-- Crear tabla CodigoBarras
CREATE TABLE codigo_barras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    tipo ENUM('EAN13','EAN8','UPC') NOT NULL,
    valor VARCHAR(20) NOT NULL UNIQUE,
    fecha_asignacion DATE,
    observaciones VARCHAR(255),
    producto_id BIGINT UNIQUE,  -- Relación 1:1 con producto
    CONSTRAINT fk_codigobarras_producto
        FOREIGN KEY (producto_id)
        REFERENCES producto(id)
        ON DELETE CASCADE
);

-- Mejorar performance
CREATE INDEX idx_producto_eliminado ON producto(eliminado);
CREATE INDEX idx_producto_nombre ON producto(nombre);
CREATE INDEX idx_codigobarras_eliminado ON codigo_barras(eliminado);
CREATE INDEX idx_codigobarras_valor ON codigo_barras(valor);

