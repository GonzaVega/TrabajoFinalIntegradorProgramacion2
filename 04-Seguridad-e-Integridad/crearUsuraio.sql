USE tpi_productos;

CREATE USER IF NOT EXISTS 'usuario_tpi'@'localhost' IDENTIFIED BY '12345678';
FLUSH PRIVILEGES;

-- 2. Dar permisos mínimos: SELECT sólo sobre las vistas públicas
GRANT SELECT ON tpi_productos.vista_productos_publico TO 'usuario_tpi'@'localhost';
GRANT SELECT ON tpi_productos.vista_codigos_publico TO 'usuario_tpi'@'localhost';

GRANT EXECUTE ON PROCEDURE tpi_productos.sp_get_producto_por_nombre TO 'usuario_tpi'@'localhost';

FLUSH PRIVILEGES;
