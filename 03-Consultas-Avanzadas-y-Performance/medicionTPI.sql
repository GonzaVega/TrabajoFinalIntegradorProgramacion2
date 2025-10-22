USE tpi_productos;

-- Con índice
EXPLAIN SELECT COUNT(*) FROM productos WHERE eliminado = FALSE;

-- Sin índice
EXPLAIN SELECT COUNT(*) FROM productos IGNORE INDEX (idx_producto_eliminado) WHERE eliminado = FALSE;


