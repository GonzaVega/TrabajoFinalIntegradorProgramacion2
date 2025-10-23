USE tpi_productos;

SELECT CONNECTION_ID();

SET autocommit = 0;
SET innodb_lock_wait_timeout = 120;

START TRANSACTION;

	SELECT * 
		FROM productos 
		WHERE id = 1 FOR UPDATE;

	UPDATE productos 
		SET precio = precio + 10 
		WHERE id = 1;

	SELECT precio 
		FROM productos 
		WHERE id = 1;

COMMIT;


SET autocommit = 1;    
SET autocommit = 0; 

START TRANSACTION;

	UPDATE productos 
		SET precio = precio + 10 
		WHERE id = 1;

	UPDATE productos 
		SET precio = precio + 10 
		WHERE id = 2;

	SELECT precio 
		FROM productos 
		WHERE id < 3;

COMMIT;


SET autocommit = 1; 

DROP TABLE IF EXISTS log_errores;

CREATE TABLE IF NOT EXISTS log_errores (
	id INT AUTO_INCREMENT PRIMARY KEY,
	id_conexion INT,
	fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	mensaje TEXT
);

SELECT nombre, precio 
	FROM productos 
	WHERE id < 3;

SET autocommit = 0;

CALL actualizar_productos_con_retry(2, 1, 5.00);

SELECT * 
	FROM log_errores 
	ORDER BY id ASC;

SELECT nombre, precio 
	FROM productos 
	WHERE id < 3;

SELECT id, fecha, mensaje 
	FROM log_errores 
	ORDER BY id ASC;
