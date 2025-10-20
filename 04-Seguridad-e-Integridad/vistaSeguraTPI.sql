USE tpi_productos;

-- Vista 1: Productos Públicos 
CREATE OR REPLACE VIEW vista_productos_publico AS
SELECT 
    p.nombre,
    p.marca,
    p.categoria,
    p.precio,
    p.peso,
    cb.tipo AS tipo_codigo,
    cb.valor AS codigo_valor
FROM productos p
JOIN codigos_barras cb ON p.codigos_barras_id = cb.id
WHERE p.eliminado = FALSE AND cb.eliminado = FALSE;

-- Vista 2: Códigos de Barras Públicos 
CREATE OR REPLACE VIEW vista_codigos_publico AS
SELECT 
    valor,
    tipo,
    'Código válido' AS estado
FROM codigos_barras 
WHERE eliminado = FALSE;