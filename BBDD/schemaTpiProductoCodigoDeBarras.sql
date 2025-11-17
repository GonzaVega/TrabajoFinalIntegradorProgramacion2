CREATE DATABASE IF NOT EXISTS tpi_productos;
USE tpi_productos;

CREATE TABLE codigos_barras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    tipo ENUM('EAN13','EAN8','UPC') NOT NULL,
    valor VARCHAR(20) NOT NULL UNIQUE,
    fecha_asignacion DATE,
    observaciones VARCHAR(255)
    );

CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    nombre VARCHAR(120) NOT NULL,
    marca VARCHAR(80),
    categoria VARCHAR(80),
    precio DECIMAL(10,2) NOT NULL,
    peso DECIMAL(10,3),
    codigos_barras_id BIGINT UNIQUE, 
    CONSTRAINT fk_producto_codigobarras
        FOREIGN KEY (codigos_barras_id)
        REFERENCES codigos_barras(id)
        ON DELETE SET NULL 
);

CREATE INDEX idx_producto_eliminado ON productos(eliminado);
CREATE INDEX idx_producto_nombre ON productos(nombre);
CREATE INDEX idx_codigobarras_eliminado ON codigos_barras(eliminado);
CREATE INDEX idx_codigobarras_valor ON codigos_barras(valor);
