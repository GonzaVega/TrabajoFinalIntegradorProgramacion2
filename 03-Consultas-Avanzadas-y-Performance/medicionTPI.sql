USE tpi_productos;

-- Ver con indice
EXPLAIN SELECT p.nombre, cb.valor
FROM productos p
JOIN codigos_barras cb ON p.codigos_barras_id = cb.id
WHERE p.categoria = 'Electrónicos';

-- Sin indice 
EXPLAIN SELECT p.nombre, cb.valor
FROM productos p IGNORE INDEX (idx_producto_nombre, idx_producto_eliminado)
JOIN codigos_barras cb IGNORE INDEX (idx_codigobarras_valor)
ON p.codigos_barras_id = cb.id
WHERE p.categoria = 'Electrónicos';
