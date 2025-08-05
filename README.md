# ecommerce-api
Proyecto API backend de un ecommerce

# Configuración necesaria
1. Crear archivo .env en la raíz del proyecto con las variables, donde XXXXXXXXX debe ser reemplazado por 
el valor que corresponda

MYSQL_ROOT_PASSWORD=XXXXXXXXX
MYSQL_DATABASE=ecommerce_db
MYSQL_USER=XXXXXXXXX
MYSQL_PASSWORD=XXXXXXXXX

SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/ecommerce_db
SPRING_DATASOURCE_USERNAME=XXXXXXXXX
SPRING_DATASOURCE_PASSWORD=XXXXXXXXX
SPRING_PROFILES_ACTIVE=prod

Importante:
- MYSQL_PASSWORD y SPRING_DATASOURCE_PASSWORD tendrán el mismo valor
- MYSQL_USER y SPRING_DATASOURCE_USERNAME tendrán el mismo valor

# Levantar contenedores
docker-compose up -d

