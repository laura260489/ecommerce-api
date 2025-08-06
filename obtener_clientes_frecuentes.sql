
CREATE PROCEDURE obtener_clientes_frecuentes()
BEGIN
   SELECT 
    CONCAT(u.first_name, ' ', u.last_name) AS nombre_completo,
    u.email AS correo,
    COUNT(o.id) AS total_ordenes
	FROM 
	    orders o
	JOIN 
	    users u ON o.user_id = u.id
	WHERE 
	    o.status = 'ACTIVE'
	GROUP BY 
	    u.id, u.first_name, u.last_name
	ORDER BY 
	    total_ordenes DESC
	LIMIT 5;
	
END;

