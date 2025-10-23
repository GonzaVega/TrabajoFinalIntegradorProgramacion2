USE tpi_productos;

SELECT CONNECTION_ID();

SET autocommit = 0;
SET innodb_lock_wait_timeout = 120;

START TRANSACTION;

	UPDATE productos 
		SET precio = precio + 5 
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
		WHERE id = 2;

	UPDATE productos 
		SET precio = precio + 10 
		WHERE id = 1;

	SELECT precio 
		FROM productos 
		WHERE id < 3;

COMMIT;


SET autocommit = 0; 

--
DROP PROCEDURE IF EXISTS actualizar_productos_con_retry;

DELIMITER $$

CREATE PROCEDURE actualizar_productos_con_retry(
	IN p_id1 INT, 
	IN p_id2 INT, 
	IN p_incremento DECIMAL(10,2)
)
BEGIN
	DECLARE v_reintentos INT DEFAULT 0;
	DECLARE v_max_reintentos INT DEFAULT 2;
	DECLARE v_done BOOLEAN DEFAULT FALSE;
	DECLARE v_error_code INT;
	DECLARE v_sqlstate CHAR(5);
	DECLARE v_mensaje TEXT;
	DECLARE v_id_conexion INT;

    -- Obtener ID de conexión
	SET v_id_conexion = CONNECTION_ID();
	SET SESSION innodb_lock_wait_timeout = 15;

	retry_loop: WHILE v_done = FALSE AND v_reintentos <= v_max_reintentos DO
		BEGIN
			DECLARE EXIT HANDLER FOR SQLEXCEPTION
			BEGIN
				GET DIAGNOSTICS CONDITION 1 
					v_sqlstate = RETURNED_SQLSTATE,
					v_error_code = MYSQL_ERRNO,
					v_mensaje = MESSAGE_TEXT;

				-- si encuentra algun problema, rollback.	
				ROLLBACK;
				
                -- Si encuentra un error 1213/SQLSATE 40001
				IF v_error_code IN (1213, 1205) OR v_sqlstate IN ('40001', 'HY000') THEN
					SET v_reintentos = v_reintentos + 1;
					
					INSERT INTO log_errores(id_conexion, mensaje)
					VALUES (
						v_id_conexion,
						CONCAT(
							IF(v_error_code = 1213, '[DEADLOCK] ', '[TIMEOUT] '),
							'Error: ', v_error_code, 
							' | SQLSTATE: ', v_sqlstate, 
							' | Reintento #', v_reintentos, ' de ', v_max_reintentos,
							' | Mensaje: ', v_mensaje
						)
					);
					
					DO SLEEP(v_reintentos);
					-- Logica para que haya dos reintentos.
					IF v_reintentos > v_max_reintentos THEN
						INSERT INTO log_errores(id_conexion, mensaje)
						VALUES (
							v_id_conexion,
							'[FALLO FINAL] Se alcanzo el maximo de reintentos (2)'
						);
						SET v_done = TRUE;
					END IF;
				ELSE
					INSERT INTO log_errores(id_conexion, mensaje)
					VALUES (
						v_id_conexion,
						CONCAT(
							'[ERROR NO RECUPERABLE] ',
							'Codigo: ', v_error_code,
							' | SQLSTATE: ', v_sqlstate, 
							' | Mensaje: ', v_mensaje
						)
					);
					SET v_done = TRUE;
				END IF;
			END;

            -- Empieza la transacción de produtos
			START TRANSACTION;
				
				INSERT INTO log_errores(id_conexion, mensaje)
				VALUES (
					v_id_conexion,
					CONCAT('[INICIO] Transaccion - Intento #', v_reintentos + 1)
				);
				
				UPDATE productos
					SET precio = precio + p_incremento
					WHERE id = p_id1;  
				
				INSERT INTO log_errores(id_conexion, mensaje)
				VALUES (
					v_id_conexion,
					CONCAT('  [UPDATE] Producto ', p_id1, ' bloqueado y actualizado')
				);
				
				DO SLEEP(8);
				
				UPDATE productos
					SET precio = precio + p_incremento
					WHERE id = p_id2;  
				
				INSERT INTO log_errores(id_conexion, mensaje)
				VALUES (
					v_id_conexion,
					CONCAT('  [UPDATE] Producto ', p_id2, ' bloqueado y actualizado')
				);
			
            -- Si sale bien, commit.    
			COMMIT;
			
			INSERT INTO log_errores(id_conexion, mensaje)
			VALUES (
				v_id_conexion,
				CONCAT(
					'[EXITO] Transaccion exitosa despues de ', 
					v_reintentos, 
					' reintento(s). ',
					'Productos ', p_id1, ' y ', p_id2, ' actualizados con +', p_incremento
				)
			);
			
			SET v_done = TRUE;
		END;
	END WHILE retry_loop;
	
	SET SESSION innodb_lock_wait_timeout = DEFAULT;
END$$

DELIMITER ;

CALL actualizar_productos_con_retry(1, 2, 5.00);

SELECT * 
	FROM log_errores 
	ORDER BY id ASC;

SELECT nombre, precio 
	FROM productos 
	WHERE id < 3;

SELECT id, fecha, mensaje 
	FROM log_errores 
	ORDER BY id ASC;

