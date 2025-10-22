USE tpi_productos;

DELIMITER $$

DROP PROCEDURE IF EXISTS GenerarDatosMasivos$$

CREATE PROCEDURE GenerarDatosMasivos(IN cantidad INT)
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE product_name VARCHAR(120);
    DECLARE product_brand VARCHAR(80);
    DECLARE product_category VARCHAR(80);
    DECLARE product_price DECIMAL(10,2);
    DECLARE product_weight DECIMAL(10,3);
    DECLARE codigo_barras_id_generado BIGINT;
    DECLARE codigo VARCHAR(20);
    
    DECLARE categorias VARCHAR(500) DEFAULT 'Electrónicos,Alimentación,Deportes,Libros,Muebles,Bebidas,Ropa,Juguetes,Hogar,Salud';
    DECLARE marcas VARCHAR(500) DEFAULT 'Sony,Samsung,LG,Nike,Adidas,Apple,Lenovo,Dell,HP,Ikea,Zara,Nestle,CocaCola,Puma,Under Armour';
    DECLARE adjetivos VARCHAR(500) DEFAULT 'Nuevo,Avanzado,Compacto,Eco,Premium,Portátil,Pro,Smart,Clásico,Ultra';
    
    SET autocommit = 0;
    SET unique_checks = 0;
    SET foreign_key_checks = 0;
    
    START TRANSACTION;
    
    WHILE i < cantidad DO
        SET product_name = CONCAT(
            SUBSTRING_INDEX(SUBSTRING_INDEX(adjetivos, ',', FLOOR(1 + RAND() * 10)), ',', -1),
            ' ',
            SUBSTRING_INDEX(SUBSTRING_INDEX(marcas, ',', FLOOR(1 + RAND() * 15)), ',', -1),
            ' ',
            SUBSTRING_INDEX(SUBSTRING_INDEX(categorias, ',', FLOOR(1 + RAND() * 10)), ',', -1)
        );
        
        SET product_brand = SUBSTRING_INDEX(SUBSTRING_INDEX(marcas, ',', FLOOR(1 + RAND() * 15)), ',', -1);
        SET product_category = SUBSTRING_INDEX(SUBSTRING_INDEX(categorias, ',', FLOOR(1 + RAND() * 10)), ',', -1);
        
        SET product_price = ROUND(
            CASE
                WHEN RAND() < 0.5 THEN RAND() * 150 + 50      
                WHEN RAND() < 0.9 THEN RAND() * 1000 + 200    
                ELSE RAND() * 4000 + 1000                     
            END, 2
        );
        
        SET product_weight = ROUND(POW(RAND(), 2) * 10, 3);
        
        SET codigo = CONCAT(
            CASE WHEN RAND() > 0.5 THEN '84' ELSE '77' END,
            LPAD(i, 10, '0')
        );

        INSERT INTO codigos_barras (tipo, valor, fecha_asignacion, eliminado) 
        VALUES (
            CASE FLOOR(RAND() * 3)
                WHEN 0 THEN 'EAN13'
                WHEN 1 THEN 'EAN8'
                ELSE 'UPC'
            END,
            codigo,
            DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY),
            FALSE
        );
        
        SET codigo_barras_id_generado = LAST_INSERT_ID();

        INSERT INTO productos (nombre, marca, categoria, precio, peso, codigos_barras_id, eliminado) 
        VALUES (product_name, product_brand, product_category, product_price, product_weight, codigo_barras_id_generado, FALSE);
        
        SET i = i + 1;
        
        IF i % 1000 = 0 THEN
            COMMIT;
            START TRANSACTION;
        END IF;
        
        IF i % 100000 = 0 THEN
            SELECT CONCAT('Insertados ', i, ' registros') AS progreso;
        END IF;
        
    END WHILE;
    
    COMMIT;
    
    SET foreign_key_checks = 1;
    SET unique_checks = 1;
    SET autocommit = 1;
END$$

DELIMITER ;

CALL GenerarDatosMasivos(700000);

