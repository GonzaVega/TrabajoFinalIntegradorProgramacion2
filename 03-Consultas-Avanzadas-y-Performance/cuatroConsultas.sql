USE tpi_productos;

-- Consulta 1: JOIN productos codigos_barras  Objetivo: lista de productos con su tipo y valor de codigo de barras.
SELECT 
    p.id AS id_producto,
    p.nombre,
    p.marca,
    p.categoria,
    p.precio,
    cb.tipo AS tipo_codigo,
    cb.valor AS valor_codigo,
    cb.fecha_asignacion
FROM productos p
JOIN codigos_barras cb 
    ON p.codigos_barras_id = cb.id
WHERE p.eliminado = FALSE
ORDER BY p.nombre;

-- Consulta 2: JOIN + rango de precios Objetivo: productos con precio mayor a 1000 agrupados por categoria.
SELECT 
    p.categoria,
    COUNT(*) AS cantidad_productos_caros,
    ROUND(AVG(p.precio),2) AS promedio_precio
FROM productos p
JOIN codigos_barras cb ON p.codigos_barras_id = cb.id
WHERE p.precio > 1000
GROUP BY p.categoria
ORDER BY promedio_precio DESC;

-- Consulta 3: GROUP BY + HAVING Objetivo: marcas que fabrican productos en más de 3 categorias distintas
SELECT 
    marca,
    COUNT(*) AS total_productos,
    COUNT(DISTINCT categoria) AS categorias_distintas
FROM productos
WHERE eliminado = FALSE
GROUP BY marca
HAVING COUNT(DISTINCT categoria) > 3
ORDER BY categorias_distintas DESC, total_productos DESC;


-- Consulta 4: Subconsulta Objetivo: productos con precio superior al promedio general.
SELECT 
    id, nombre, categoria, precio
FROM productos
WHERE precio > (SELECT AVG(precio) FROM productos)
ORDER BY precio DESC
LIMIT 50;
