USE tpi_productos;

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_get_producto_por_nombre$$

CREATE PROCEDURE sp_get_producto_por_nombre(IN nombre_buscado VARCHAR(120))
BEGIN
    SELECT 
        p.nombre, 
        p.marca, 
        p.categoria, 
        p.precio, 
        cb.valor AS codigo_barras
    FROM productos p
    JOIN codigos_barras cb ON p.codigos_barras_id = cb.id
    WHERE p.nombre LIKE CONCAT('%', nombre_buscado, '%')
      AND p.eliminado = FALSE;
END$$

DELIMITER ;