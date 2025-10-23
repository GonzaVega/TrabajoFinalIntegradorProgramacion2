USE tpi_productos;

UPDATE productos 
	SET precio = 150.00 
	WHERE id = 1;

SELECT id, nombre, precio 
	FROM productos 
	WHERE id = 1;

COMMIT;

SELECT id, nombre, precio 
	FROM productos 
	WHERE id = 1;
