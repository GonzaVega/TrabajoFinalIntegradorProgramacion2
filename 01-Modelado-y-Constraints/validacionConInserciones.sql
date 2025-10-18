USE tpi_productos;

-- Insercion correcta de un codigo de barras y producto
INSERT INTO codigos_barras (tipo, valor, fecha_asignacion, observaciones)
VALUES ('EAN13', '1234567890123', '2024-02-10', 'Prueba OK');

INSERT INTO productos (nombre, marca, categoria, precio, peso, codigos_barras_id)
VALUES ('Smartwatch X', 'Samsung', 'Electrónicos', 250.00, 0.150, 1);

-- Otra insercion correcta
INSERT INTO codigos_barras (tipo, valor, fecha_asignacion)
VALUES ('UPC', '987654321000', '2024-02-12');

INSERT INTO productos (nombre, marca, categoria, precio, peso, codigos_barras_id)
VALUES ('Cafetera Premium', 'Philips', 'Hogar', 89.99, 2.300, 2);

-- Error 1: violacion de UNIQUE (valor repetido en codigo de barras)
INSERT INTO codigos_barras (tipo, valor, fecha_asignacion)
VALUES ('EAN8', '1234567890123', '2024-02-13');
-- Dara error: Duplicate entry '1234567890123' for key 'valor'

-- Error 2: violacion de CHECK (precio negativo)
INSERT INTO productos (nombre, marca, categoria, precio, peso, codigos_barras_id)
VALUES ('Producto Fallido', 'LG', 'Electrónicos', -99.99, 0.500, 2);
-- Dara error: CHECK constraint 'productos_chk_1' failed
