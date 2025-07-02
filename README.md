# API de Ventas - Perfunlandia

API REST para gestión de ventas con soporte HATEOAS y documentación Swagger.

## Características

- ✅ **HATEOAS**: Enlaces hipermedia para navegación entre recursos
- ✅ **Swagger/OpenAPI**: Documentación interactiva de la API
- ✅ **Spring Boot 3.4.0**: Versión estable más reciente
- ✅ **Validación**: Validación de datos de entrada
- ✅ **MySQL**: Base de datos persistente

## Endpoints Disponibles

### Endpoints Principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/ventas` | Listar todas las ventas con enlaces HATEOAS |
| GET | `/ventas/{id}` | Obtener venta específica por ID |
| POST | `/ventas` | Crear nueva venta |
| DELETE | `/ventas/{id}` | Eliminar venta por ID |

### Endpoints Adicionales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/ventas/stats` | Obtener estadísticas de ventas |
| GET | `/ventas/cliente/{idCliente}` | Buscar ventas por cliente |

### Endpoints Integrados con Detalle Ventas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/ventas/{id}/con-detalles` | Obtener venta con todos sus detalles de productos |
| GET | `/ventas/stats/completas` | Estadísticas combinadas de ventas y productos |
| GET | `/ventas/productos/mas-vendidos` | Productos más vendidos desde Detalle Ventas |

## Documentación Swagger

- **URL**: http://localhost:8181/swagger-ui.html
- **API Docs**: http://localhost:8181/api-docs

## Enlaces HATEOAS

La API incluye enlaces HATEOAS que apuntan tanto al microservicio directo como al API Gateway:

- **Self**: Enlace al recurso actual
- **ventas**: Enlace a la colección de ventas
- **delete**: Enlace para eliminar el recurso (cuando aplica)
- **gateway**: Enlace al API Gateway (puerto 8888)

### Ejemplo de Respuesta HATEOAS

```json
{
  "id_venta": 1,
  "id_cliente": 123,
  "id_vendedor": 456,
  "fechaVenta": "2024-01-15",
  "total": 150.50,
  "id_metodopago": 1,
  "_links": {
    "self": {
      "href": "http://localhost:8181/ventas/1"
    },
    "ventas": {
      "href": "http://localhost:8181/ventas"
    },
    "delete": {
      "href": "http://localhost:8181/ventas/1"
    },
    "gateway": {
      "href": "http://localhost:8888/ventas/1"
    }
  }
}
```

## Configuración

### Base de Datos
- **URL**: `jdbc:mysql://localhost:3306/perfunlandia_db`
- **Usuario**: `root`
- **Contraseña**: (vacía)

### Puertos
- **API Ventas**: 8181
- **API Detalle Ventas**: 8082
- **API Reportes**: 8080
- **API Gateway**: 8888

## Ejecución

1. **Compilar el proyecto**:
   ```bash
   mvn clean compile
   ```

2. **Ejecutar la aplicación**:
   ```bash
   mvn spring-boot:run
   ```

3. **Acceder a la documentación**:
   - Swagger UI: http://localhost:8181/swagger-ui.html
   - API Docs: http://localhost:8181/api-docs

## Estructura del Proyecto

```
src/main/java/com/api/spring/boot/ventas/
├── controller/
│   └── VentaController.java      # Controlador REST con HATEOAS y Swagger
├── model/
│   └── Venta.java               # Entidad JPA
├── dto/
│   └── VentaDTO.java            # DTO con soporte HATEOAS
├── service/
│   └── VentaService.java        # Lógica de negocio
├── repository/
│   └── VentaRepository.java     # Repositorio JPA
├── config/
│   └── OpenApiConfig.java       # Configuración Swagger
└── VentaApplication.java        # Clase principal
```

## Dependencias Agregadas

- `spring-boot-starter-hateoas`: Soporte para HATEOAS
- `springdoc-openapi-starter-webmvc-ui`: Documentación Swagger/OpenAPI
- Spring Boot actualizado a versión 3.4.0

## Arquitectura de Microservicios

```
┌─────────────────┐    ←→    ┌─────────────────┐
│   API Ventas    │          │ API Detalle     │
│   (Puerto 8181) │          │   Ventas        │
│                 │          │  (Puerto 8082)  │
└─────────────────┘          └─────────────────┘
         │                           │
         └───────────┬───────────────┘
                     │
         ┌─────────────────┐
         │  API Reportes   │
         │  (Puerto 8080)  │
         │  (Agregador)    │
         └─────────────────┘
                     │
         ┌─────────────────┐
         │  API Gateway    │
         │  (Puerto 8888)  │
         └─────────────────┘
```

## Notas de Implementación

- Los enlaces HATEOAS apuntan tanto al microservicio directo como al API Gateway
- Se agregaron endpoints adicionales para estadísticas y búsqueda por cliente
- La documentación Swagger incluye ejemplos y descripciones detalladas
- Se mantiene la compatibilidad con el código existente
- **Integración con Detalle Ventas**: La API de Ventas se comunica con Detalle Ventas mediante RestTemplate
- **Endpoints combinados**: Se crearon endpoints que combinan datos de ambos microservicios
- **Manejo de errores**: Si Detalle Ventas no está disponible, se retornan datos parciales 