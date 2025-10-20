USE tpi_productos;

-- 1) Conteos generales
SELECT COUNT(*) AS total_productos FROM productos;
SELECT COUNT(*) AS total_codigos FROM codigos_barras;

-- 2) ¿Cuántos productos tienen código asociado?
SELECT COUNT(*) AS productos_con_codigo
FROM productos
WHERE codigos_barras_id IS NOT NULL;

-- 3) ¿Cuántos códigos están referenciados por productos?
SELECT COUNT(DISTINCT cb.id) AS codigos_referenciados
FROM codigos_barras cb
JOIN productos p ON p.codigos_barras_id = cb.id;

-- 4) Verificar correspondencia 1:1 esperada 
-- número de pares producto<->codigo por JOIN
SELECT COUNT(*) AS pares_producto_codigo
FROM productos p
JOIN codigos_barras cb ON p.codigos_barras_id = cb.id;

-- 5) Buscar FKs huérfanas (productos que referencian un id de codigo que no existe)
SELECT p.codigos_barras_id, COUNT(*) AS cantidad
FROM productos p
LEFT JOIN codigos_barras cb ON p.codigos_barras_id = cb.id
WHERE p.codigos_barras_id IS NOT NULL AND cb.id IS NULL
GROUP BY p.codigos_barras_id
LIMIT 20;

-- 6) Buscar códigos sin producto (codigos_barras no referenciados por productos)
SELECT cb.id, cb.valor
FROM codigos_barras cb
LEFT JOIN productos p ON p.codigos_barras_id = cb.id
WHERE p.codigos_barras_id IS NULL
LIMIT 20;

-- 7) Verificación de unicidad del valor del código de barras (debería ser 1 por valor)
SELECT valor, COUNT(*) AS cnt
FROM codigos_barras
GROUP BY valor
HAVING cnt > 1
LIMIT 20;

-- 8) Distribución por categoría (productos)
SELECT categoria, COUNT(*) AS cantidad, ROUND(100 * COUNT(*) / (SELECT COUNT(*) FROM productos),2) AS pct
FROM productos
GROUP BY categoria
ORDER BY cantidad DESC;

-- 9) Distribución por tipo de código (codigos_barras)
SELECT tipo, COUNT(*) AS cantidad, ROUND(100 * COUNT(*) / (SELECT COUNT(*) FROM codigos_barras),2) AS pct
FROM codigos_barras
GROUP BY tipo;

-- 10) Estadísticos de precio y peso
SELECT
    COUNT(*) AS total,
    MIN(precio) AS min_precio,
    ROUND(AVG(precio),2) AS avg_precio,
    MAX(precio) AS max_precio,
    SUM(CASE WHEN precio <= 0 THEN 1 ELSE 0 END) AS cant_precios_incorrectos
FROM productos;

SELECT
    COUNT(*) AS total,
    MIN(peso) AS min_peso,
    ROUND(AVG(peso),3) AS avg_peso,
    MAX(peso) AS max_peso,
    SUM(CASE WHEN peso < 0 THEN 1 ELSE 0 END) AS cant_pesos_negativos
FROM productos;

-- 11) Comprobar nombres vacíos o NULL
SELECT COUNT(*) AS nombres_vacios_o_null
FROM productos
WHERE nombre IS NULL OR TRIM(nombre) = '';

-- 12) JOIN de muestreo: mostrar algunos productos con su código de barras
SELECT p.id AS producto_id, p.nombre, p.marca, p.categoria, p.precio, p.peso,
       cb.id AS codigo_id, cb.tipo, cb.valor, cb.fecha_asignacion
FROM productos p
LEFT JOIN codigos_barras cb ON p.codigos_barras_id = cb.id
ORDER BY p.id
LIMIT 50;

-- 13) Verificar que no existan duplicados de codigos_barras_id en productos 
SELECT codigos_barras_id, COUNT(*) AS cnt
FROM productos
WHERE codigos_barras_id IS NOT NULL
GROUP BY codigos_barras_id
HAVING cnt > 1
LIMIT 20;

-- 14) Comprobación de integridad rápida: conteos cruzados esperables
-- Si cada producto genera 1 codigo y viceversa, total_productos == total_codigos == pares_producto_codigo
SELECT
    (SELECT COUNT(*) FROM productos) AS total_productos,
    (SELECT COUNT(*) FROM codigos_barras) AS total_codigos,
    (SELECT COUNT(*) FROM productos p JOIN codigos_barras cb ON p.codigos_barras_id = cb.id) AS pares_join;

-- 15) Pruebas para constraints CHECK aparentes (buscar violaciones lógicas)
-- Precios <= 0 (deberían ser 0 resultados si CHECK (precio > 0) está activo)
SELECT id, nombre, precio FROM productos WHERE precio <= 0 LIMIT 50;

-- Pesos negativos
SELECT id, nombre, peso FROM productos WHERE peso < 0 LIMIT 50;

-- Categorías fuera del dominio definido (si aplicaste CHECK con lista fixa)
SELECT DISTINCT categoria FROM productos
WHERE categoria NOT IN ('Electrónicos','Alimentación','Deportes','Libros','Muebles','Bebidas','Ropa','Juguetes','Hogar','Salud');

-- 16) Comprobar índices. Muestra índices de las tablas
SHOW INDEX FROM productos;
SHOW INDEX FROM codigos_barras;
