USE tpi_productos;

-- Verificar datos generados
SELECT COUNT(*) as total_productos FROM producto;
SELECT COUNT(*) as total_codigos FROM codigo_barras;

-- Ver distribución por categoría
SELECT categoria, COUNT(*) as cantidad 
FROM producto 
GROUP BY categoria;

-- Ver distribución por tipo de código
SELECT tipo, COUNT(*) as cantidad 
FROM codigo_barras 
GROUP BY tipo;