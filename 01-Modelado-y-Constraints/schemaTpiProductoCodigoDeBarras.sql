-- Crear base de datos
CREATE DATABASE IF NOT EXISTS tpi_productos;
USE tpi_productos;

-- Tabla de códigos de barras
CREATE TABLE codigos_barras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    tipo ENUM('EAN13','EAN8','UPC') NOT NULL,
    valor VARCHAR(20) NOT NULL UNIQUE,
    fecha_asignacion DATE,
    observaciones VARCHAR(255),
    CHECK (LENGTH(valor) > 0)
);

-- Tabla de productos (relación 1 a 1 con código de barras)
CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nombre VARCHAR(120) NOT NULL,
    marca VARCHAR(80),
    categoria VARCHAR(80),
    precio DECIMAL(10,2) NOT NULL,
    peso DECIMAL(10,3),
    codigos_barras_id BIGINT UNIQUE,
    
    -- 🔍 CHECKS añadidos
    CHECK (precio > 0),
    CHECK (peso >= 0),
    CHECK (nombre <> ''),
    CHECK (categoria IN ('Electrónicos','Alimentación','Deportes','Libros','Muebles','Bebidas','Ropa','Juguetes','Hogar','Salud')),

    CONSTRAINT fk_producto_codigobarras
        FOREIGN KEY (codigos_barras_id)
        REFERENCES codigos_barras(id)
        ON DELETE SET NULL
);

-- Índices para performance
CREATE INDEX idx_producto_eliminado ON productos(eliminado);
CREATE INDEX idx_producto_nombre ON productos(nombre);
CREATE INDEX idx_codigobarras_eliminado ON codigos_barras(eliminado);
CREATE INDEX idx_codigobarras_valor ON codigos_barras(valor);
