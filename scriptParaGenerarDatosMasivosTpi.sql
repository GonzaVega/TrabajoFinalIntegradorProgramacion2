USE tpi_productos;

-- Procedimiento para generar datos masivos
DELIMITER $$
CREATE PROCEDURE GenerarDatosMasivos(IN cantidad INT)
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE product_name VARCHAR(120);
    DECLARE product_brand VARCHAR(80);
    DECLARE product_category VARCHAR(80);
    DECLARE product_price DECIMAL(10,2);
    DECLARE product_weight DECIMAL(10,3);
    DECLARE product_id BIGINT;
    
    DECLARE categorias VARCHAR(500) DEFAULT 'Electrónicos,Alimentación,Deportes,Libros,Muebles,Bebidas,Ropa,Juguetes,Hogar,Salud';
    DECLARE marcas VARCHAR(500) DEFAULT 'Sony,Samsung,LG,Nike,Adidas,Apple,Lenovo,Dell,HP,Ikea,Zara,Nestle,CocaCola,Puma,Under Armour';
    
    WHILE i < cantidad DO
        SET product_name = CONCAT('Producto ', i+1);
        SET product_brand = SUBSTRING_INDEX(SUBSTRING_INDEX(marcas, ',', FLOOR(1 + RAND() * 15)), ',', -1);
        SET product_category = SUBSTRING_INDEX(SUBSTRING_INDEX(categorias, ',', FLOOR(1 + RAND() * 10)), ',', -1);
        SET product_price = ROUND(RAND() * 1000 + 1, 2);
        SET product_weight = ROUND(RAND() * 10, 3);
        
        INSERT INTO producto (nombre, marca, categoria, precio, peso) 
        VALUES (product_name, product_brand, product_category, product_price, product_weight);
        
        SET product_id = LAST_INSERT_ID();
        
        INSERT INTO codigo_barras (tipo, valor, fecha_asignacion, producto_id) 
        VALUES (
            CASE FLOOR(RAND() * 3)
                WHEN 0 THEN 'EAN13'
                WHEN 1 THEN 'EAN8' 
                ELSE 'UPC'
            END,
            CONCAT(
                CASE 
                    WHEN RAND() > 0.5 THEN '84' ELSE '77' 
                END,
                LPAD(FLOOR(RAND() * 10000000000), 10, '0')
            ),
            DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY),
            product_id
        );
        
        SET i = i + 1;
        
        IF i % 1000 = 0 THEN
            SELECT CONCAT('Insertados ', i, ' registros') AS progreso;
        END IF;
    END WHILE;
    
    SELECT CONCAT('Finalizado: ', cantidad, ' productos generados') AS resultado;
END$$
DELIMITER ;

-- Ejecutar para generar datos
-- Para 10,000 registros:
CALL GenerarDatosMasivos(10000);
