USE tpi_productos;

CREATE OR REPLACE VIEW vista_productos_detalle AS
SELECT 
    p.id AS producto_id,
    p.nombre,
    p.marca,
    p.categoria,
    p.precio,
    p.peso,
    cb.tipo AS tipo_codigo,
    cb.valor AS codigo_valor,
    cb.fecha_asignacion
FROM productos p
JOIN codigos_barras cb ON p.codigos_barras_id = cb.id
WHERE p.eliminado = FALSE AND cb.eliminado = FALSE;

SELECT * FROM vista_productos_detalle LIMIT 10;
