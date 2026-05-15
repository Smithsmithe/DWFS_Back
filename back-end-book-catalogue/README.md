# Book Catalogue Microservice

Microservicio desarrollado con Spring Boot siguiendo arquitectura en capas para la gestión del catálogo de libros dentro de una arquitectura de microservicios.

Este servicio permite administrar libros, descripciones bibliográficas e imágenes asociadas, exponiendo una API REST documentada con Swagger/OpenAPI.

## Tecnologías utilizadas

- Java 25
- Spring Boot 4
- Spring Data JPA
- MySQL
- Spring Cloud Netflix Eureka Client
- Lombok
- Maven
- Swagger / OpenAPI
- Postman

---

## Arquitectura

El proyecto sigue arquitectura en capas:

```text
src/main/java/com/unir/book/catalogue
├── config
├── controller
│   └── model
├── entity
├── exceptions
├── repository
│   └── predicate
└── service
    ├── interfaces
    └── impl
```

### Capas

**Controller**
- Exposición de endpoints REST.
- Validación de requests.
- Integración con Swagger/OpenAPI.

**Service**
- Implementación de lógica de negocio.
- Validación de ISBN duplicado.
- Conversión entidad → response model.
- Gestión de imágenes principales.

**Repository**
- Persistencia con Spring Data JPA.
- Specifications dinámicas para filtros avanzados.
- Consultas optimizadas con EntityGraph.

**Entity**
- Mapeo ORM con Hibernate/JPA.

**Exceptions**
- Manejo centralizado de errores con respuestas REST consistentes.

---

## Modelo de dominio

### Entidades principales

- **Book**
    - Formato
    - Precio
    - Stock
    - Visibilidad
    - Relación con descripción

- **BookDescription**
    - Título
    - Autor
    - ISBN
    - Categoría
    - Fecha de publicación
    - Editorial
    - Idioma
    - Descripción corta y completa
    - Rating

- **BookImage**
    - URL de imagen
    - Indicador de imagen principal

---

## Funcionalidades implementadas

### Gestión de catálogo
- Crear libros
- Consultar todos los libros
- Consultar libro por ID
- Actualización completa (PUT)
- Actualización parcial (PATCH)
- Eliminación de libros

### Búsqueda dinámica
Filtros soportados:

- title
- author
- isbn
- category
- publicationDate
- rating
- visible
- format
- maxPrice
- minStock

Ejemplo:

```http
GET /api/books/v1?author=rowling&category=fantasy&rating=4.5
```

### Paginación
```http
GET /api/books/v1/paged?page=0&size=10
```

### Validaciones
- ISBN único
- Recurso no encontrado
- Manejo de errores REST consistente

---

## API Endpoints

Base URL:

```text
http://localhost:8081/api/books/v1
```

### Obtener todos los libros
```http
GET /api/books/v1
```

### Obtener libro por ID
```http
GET /api/books/v1/{id}
```

### Búsqueda con filtros
```http
GET /api/books/v1?author=rowling
```

### Paginación
```http
GET /api/books/v1/paged?page=0&size=10
```

### Crear libro
```http
POST /api/books/v1
```

### Actualización completa
```http
PUT /api/books/v1/{id}
```

### Actualización parcial
```http
PATCH /api/books/v1/{id}
```

### Eliminar libro
```http
DELETE /api/books/v1/{id}
```

---

## Swagger / OpenAPI

Documentación interactiva disponible en:

```text
http://localhost:8081/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8081/v3/api-docs
```

Permite:

- Explorar endpoints
- Ejecutar pruebas directamente
- Validar request/response payloads
- Revisar contratos REST

---

## Postman Collection

Se incluye colección Postman para pruebas manuales:

```text
back-end-book-catalogue/postam/book-catalogue-api-v1.postman_collection.json
```

Incluye:

- GET all
- GET by id
- búsquedas dinámicas
- paginación
- POST
- PUT
- PATCH
- DELETE
- pruebas de error

---

## Configuración

Archivo:

```text
src/main/resources/application.yml
```

Ejemplo:

```yaml
spring:
  application:
    name: catalogue

  datasource:
    url: jdbc:mysql://localhost:3308/book_catalogue_db
    username: root
    password: mysql

  jpa:
    show-sql: true
    open-in-view: false

    hibernate:
      ddl-auto: validate

server:
  port: 8081

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

---

## Base de datos

Motor utilizado:

```text
MySQL
```

Base de datos:

```text
book_catalogue_db
```
Implementacion Docker:

```text
- docker pull mysql
- docker run -p 3308:3306 --name dwfs-book-catalogue -e MYSQL_ROOT_PASSWORD=mysql -d mysql:latest
```

Se requiere esquema previamente creado con su carga de datos usar:

```text
back-end-book-catalogue\src\main\resources\db\schema&data.sql
```
---

## Registro en Eureka

El microservicio se registra automáticamente en Eureka Server.

Configuración:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

Verificación:

```text
http://localhost:8761
```

Servicio registrado:

```text
CATALOGUE
```

---

## Manejo de errores

Formato estándar:

```json
{
  "message": "Book not found",
  "path": "/api/books/v1/999",
  "status": 404,
  "timestamp": "2026-05-15T10:20:00"
}
```

Errores implementados:

- 404 Not Found
- 409 Conflict
- 500 Internal Server Error

---

## Ejemplo de request

### POST Create Book

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "shortDescription": "Software engineering best practices",
  "fullDescription": "A handbook of agile software craftsmanship.",
  "isbn": "9780132350884",
  "category": "Software",
  "publicationDate": "2008-08-01",
  "language": "English",
  "editorial": "Prentice Hall",
  "pages": 464,
  "rating": 4.8,
  "format": "Paperback",
  "price": 59.99,
  "stock": 10,
  "visible": true,
  "imageUrls": [
    "https://example.com/clean-code-1.jpg",
    "https://example.com/clean-code-2.jpg"
  ]
}
```

---

## Ejecución local

### Compilar
```bash
mvn clean compile
```

### Ejecutar
```bash
mvn spring-boot:run
```

---

## Estado del proyecto

Implementado:

- Arquitectura en capas
- CRUD completo
- búsqueda dinámica con Specifications
- paginación
- validaciones
- Swagger/OpenAPI
- Postman collection
- manejo global de excepciones
- integración con Eureka
- persistencia MySQL

---