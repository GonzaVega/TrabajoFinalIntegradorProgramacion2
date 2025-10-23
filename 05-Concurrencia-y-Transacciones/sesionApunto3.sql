USE tpi_productos;

UPDATE productos 
	SET precio = 100.00 
	WHERE id = 1;

SELECT nombre, precio 
	FROM productos 
	WHERE id = 1;

-- Non-Repeatable Read (Read Committed)
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;

SELECT @@transaction_isolation;

START TRANSACTION;

	SELECT id, nombre, precio 
		FROM productos 
		WHERE id = 1;

COMMIT;

SELECT id, nombre, precio 
	FROM productos 
	WHERE id = 1;

-- Repeatable Read
UPDATE productos 
	SET precio = 100.00 
	WHERE id = 1;

COMMIT;

SELECT nombre, precio 
	FROM productos 
	WHERE id = 1;

-- Repeatable Read
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;

SELECT @@transaction_isolation;

START TRANSACTION;

	SELECT id, nombre, precio 
		FROM productos 
		WHERE id = 1;

COMMIT;

SELECT id, nombre, precio 
	FROM productos 
	WHERE id = 1;
