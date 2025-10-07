USE tpi_productos;

-- Insertar productos
INSERT INTO producto (nombre, marca, categoria, precio, peso) VALUES
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
INSERT INTO codigo_barras (tipo, valor, fecha_asignacion, observaciones, producto_id) VALUES
('EAN13', '8412345678901', '2024-01-15', 'Código asignado para venta internacional', 1),
('EAN13', '8806092000014', '2024-01-16', 'Modelo 2024 - Edición limitada', 2),
('EAN8', '12345670', '2024-01-10', 'Producto de consumo rápido', 3),
('EAN13', '8872765781920', '2024-01-20', 'Colección verano 2024', 4),
('EAN13', '9788499891234', '2024-01-05', 'ISBN asignado por editorial', 5),
('EAN13', '7622210889781', '2024-01-12', 'Lote producción enero 2024', 6),
('UPC', '885909123456', '2024-01-18', 'Mercado norteamericano', 7),
('EAN13', '7391586423015', '2024-01-22', 'Stock almacén central', 8),
('EAN13', '8410312345678', '2024-01-08', 'Denominación de origen protegida', 9),
('EAN13', '4905524931234', '2024-01-25', 'Nueva línea de audio', 10);