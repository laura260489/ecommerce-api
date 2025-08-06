CREATE PROCEDURE obtener_productos_mas_vendidos()
BEGIN
    SELECT 
    p.id AS producto_id,
    p.title AS nombre_producto,
    c.name AS categoria,
    SUM(oi.quantity) AS total_vendido
	FROM 
	    order_items oi
	JOIN 
	    orders o ON oi.order_id = o.id
	JOIN 
	    product p ON oi.product_id = p.id
	JOIN
	    product_category pc ON p.id = pc.product_id
	JOIN
	    category c ON pc.category_id = c.id
	WHERE 
	    o.status = 'ACTIVE'
	GROUP BY 
	    p.id, p.title, c.name
	ORDER BY 
	    total_vendido DESC
	LIMIT 5;

END;


