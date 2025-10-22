USE tpi_productos;

-- Se inserta un Código de Barras válido 
INSERT INTO codigos_barras (tipo, valor, fecha_asignacion)
VALUES ('EAN13', '770000000007', CURDATE());

-- Se inserta un Producto válido
INSERT INTO productos (nombre, marca, categoria, precio, peso, codigos_barras_id)
VALUES ('Auriculares BT', 'JBL', 'Electrónicos', 55.99, 0.350, 3);

-- Intento de inserción con precio negativo (-9.99)
INSERT INTO productos (nombre, marca, categoria, precio, peso, codigos_barras_id)
VALUES ('Bicicleta Fallida', 'Generico', 'Deportes', -9.99, 15.000, NULL);

-- Intentamos insertar un producto referenciando un codigos_barras_id que NO existe 
INSERT INTO productos (nombre, marca, categoria, precio, peso, codigos_barras_id)
VALUES ('Producto con FK rota', 'Marca XYZ', 'Alimentación', 10.50, 0.500, 999999);