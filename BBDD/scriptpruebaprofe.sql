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

INSERT INTO productos (nombre, marca, categoria, precio, peso) VALUES
('Laptop Gaming Pro', 'TechMaster', 'Electrónicos', 1299.99, 2.300),
('Smartphone Galaxy X', 'Samsung', 'Electrónicos', 899.50, 0.185),
('Aceite de Oliva Extra', 'La Española', 'Alimentación', 8.75, 0.750),
('Zapatillas Running', 'Nike', 'Deportes', 120.00, 0.450),
('Libro Cocina Mediterránea', 'Culinary Arts', 'Libros', 24.95, 0.680),
('Café Molido Premium', 'Starbucks', 'Alimentación', 12.30, 0.500),
('Tablet Android Pro', 'Lenovo', 'Electrónicos', 349.99, 0.520),
('Silla Oficina Ergonómica', 'Ikea', 'Muebles', 189.00, 12.500),
('Vino Tinto Reserva', 'Marqués de Riscal', 'Bebidas', 18.40, 1.125),
('Auriculares Bluetooth', 'Sony', 'Electrónicos', 79.99, 0.230);

-- Insertar códigos de barras
INSERT INTO codigo_barras (tipo, valor, fecha_asignacion, observaciones) VALUES
('EAN13', '8412345678901', '2024-01-15', 'Código asignado para venta internacional'),
('EAN13', '8806092000014', '2024-01-16', 'Modelo 2024 - Edición limitada'),
('EAN8', '12345670', '2024-01-10', 'Producto de consumo rápido'),
('EAN13', '8872765781920', '2024-01-20', 'Colección verano 2024'),
('EAN13', '9788499891234', '2024-01-05', 'ISBN asignado por editorial'),
('EAN13', '7622210889781', '2024-01-12', 'Lote producción enero 2024'),
('UPC', '885909123456', '2024-01-18', 'Mercado norteamericano'),
('EAN13', '7391586423015', '2024-01-22', 'Stock almacén central'),
('EAN13', '8410312345678', '2024-01-08', 'Denominación de origen protegida'),
('EAN13', '4905524931234', '2024-01-25', 'Nueva línea de audio');