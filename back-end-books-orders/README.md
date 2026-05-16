# back-end-books-orders

Microservicio de pedidos de la aplicación **UNIR Books**. Gestiona la creación y consulta de órdenes de compra, comunicándose con el microservicio de catálogo para validar libros y actualizar stock.

## Tabla de contenidos

- [Arquitectura general](#arquitectura-general)
- [Capa controladora (Controller)](#capa-controladora)
- [Capa de servicio (Service)](#capa-de-servicio)
- [Facade — Comunicación entre microservicios](#facade--comunicación-entre-microservicios)
- [Capa de acceso a datos (Repository)](#capa-de-acceso-a-datos)
- [Modelo relacional de base de datos](#modelo-relacional-de-base-de-datos)
- [Reconstrucción de la base de datos para pruebas](#reconstrucción-de-la-base-de-datos-para-pruebas)
- [Configuración](#configuración)

---

## Arquitectura general

El microservicio sigue una arquitectura en capas con un patrón **Facade** para la comunicación inter-servicio:

```
Controller → Service → Facade (RestClient) → books-catalogue
                     → Repository → Base de datos MySQL
```

Se registra en **Eureka** como `orders` y utiliza **RestClient** con `@LoadBalanced` para resolver las URLs de otros microservicios vía Service Discovery. Escucha en el **puerto 8080**.

---

## Capa controladora

### `OrdersController` — `/api/v1/`

| Método | Endpoint         | Descripción                                                                 | Request Body            | Response                 | HTTP Status    |
|--------|------------------|-----------------------------------------------------------------------------|-------------------------|--------------------------|----------------|
| `GET`  | `/api/v1/orders` | Obtiene las 5 órdenes más recientes del usuario. Admite filtrado por estado mediante el parámetro opcional `?status=` | —                       | `GetOrdersResponseDto`   | `200 OK`       |
| `POST` | `/api/v1/orders` | Crea una nueva orden de compra                                               | `CreateOrderRequestDto` | `CreateOrderResponseDto` | `201 Created`  |

### Manejo de errores — `OrdersControllerAdvice`

| Excepción                        | HTTP Status                 | Descripción                                          |
|----------------------------------|-----------------------------|------------------------------------------------------|
| `BookNotFoundException`          | `404 Not Found`             | El libro solicitado no existe en el catálogo          |
| `BadBookModificationException`   | `400 Bad Request`           | Error al intentar modificar el stock del libro        |
| `InternalErrorException`         | `500 Internal Server Error` | Error interno al comunicarse con el catálogo          |
| `MethodArgumentTypeMismatchException` | `400 Bad Request`      | Valor inválido en un parámetro de la petición (p.ej. valor no válido para el enum `status`). La respuesta incluye los valores aceptados. |

Respuesta de error:
```json
{
  "details": "Book with ID 42 not found"
}
```

### DTOs

| DTO                       | Uso                                                                    |
|---------------------------|------------------------------------------------------------------------|
| `CreateOrderRequestDto`   | Cuerpo de creación de orden: lista de `RequestedBook`                  |
| `RequestedBook`           | Libro solicitado: `id` (del catálogo) y `quantity`                     |
| `CreateOrderResponseDto`  | Respuesta de creación: `name` (identificador de la orden)              |
| `GetOrdersResponseDto`    | Lista de `RecentOrder` (órdenes recientes o filtradas por estado)      |
| `RecentOrder`             | Detalle de orden: `id`, `date`, `status`, `total`, `comment`, `items`  |
| `PurchasedItem`           | Ítem comprado: `title`, `quantity`, `price`                            |
| `ErrorResponse`           | Respuesta de error genérica                                            |

---

## Capa de servicio

### `CreateOrdersService`

Orquesta el flujo completo de creación de una orden (`@Transactional`):

1. **Valida** que la solicitud contenga al menos un libro.
2. **Para cada libro solicitado**:
   - Valida que la cantidad sea > 0.
   - Consulta el catálogo vía `BooksCatalogueFacade.getBook()` para obtener precio y stock actual.
   - Verifica que haya stock suficiente.
   - Calcula el subtotal (`precio × cantidad`).
3. **Genera** un nombre de orden único (`ORDER-{timestamp}`).
4. **Persiste** la orden con sus ítems (cascade).
5. **Actualiza el stock** de cada libro en el catálogo vía `BooksCatalogueFacade.updateBookStock()` (PATCH).
6. **Retorna** el nombre de la orden creada.

> **Nota**: El `ownerId` está hardcodeado a `1`. Debería obtenerse del contexto de seguridad.

### `GetOrdersService`

- `getRecentOrders()`: Obtiene las **5 órdenes más recientes** del usuario (ordenadas por fecha descendente). Para cada ítem de la orden, consulta al catálogo para obtener el título y precio actual del libro.
- `getOrdersByStatus(OrderStatus status)`: Obtiene todas las órdenes del usuario filtradas por estado. Igualmente enriquece cada ítem con los datos del catálogo.

---

## Facade — Comunicación entre microservicios

### `BooksCatalogueFacade`

Componente que encapsula las llamadas HTTP al microservicio **books-catalogue** usando `RestClient` (con `@LoadBalanced` para service discovery):

| Método                                        | HTTP    | Endpoint del catálogo       | Descripción                           |
|-----------------------------------------------|---------|-----------------------------|---------------------------------------|
| `getBook(Integer bookId)`                     | `GET`   | `/api/books/v1/{id}`        | Obtiene datos de un libro             |
| `updateBookStock(Integer bookId, Integer stock)` | `PATCH` | `/api/books/v1/{id}`     | Actualiza el stock del libro          |

Manejo de errores HTTP:
- **404** → `BookNotFoundException`
- **400** → `BadBookModificationException`
- **500** → `InternalErrorException`

### `BookDto` (Facade Model)

Modelo simplificado del libro recibido del catálogo: `id`, `title`, `author`, `price`, `stock`.

### Configuración del RestClient

```java
@LoadBalanced
@Bean("loadBalancedRestClient")
public RestClient.Builder loadBalancedRestClient() { ... }
```

La anotación `@LoadBalanced` permite resolver el nombre del servicio (`catalogue`) registrado en Eureka en lugar de usar URLs hardcodeadas. Se define también un bean `plainRestClient` sin balanceo de carga marcado como `@Primary`.

---

## Capa de acceso a datos

### `OrderJpaRepository`

| Método                                                    | Tipo     | Descripción                                                    |
|-----------------------------------------------------------|----------|----------------------------------------------------------------|
| `findByOwnerIdOrderByOrderDateDesc(Integer, Limit)`       | Derivada | Órdenes de un usuario, ordenadas por fecha desc, con límite    |
| `findByStatus(OrderStatus)`                               | Derivada | Órdenes filtradas por estado                                   |

Hereda de `JpaRepository<Order, Integer>`.

### Entidades JPA

| Entidad     | Tabla        | Campos principales                                                         |
|-------------|--------------|----------------------------------------------------------------------------|
| `Order`     | `orders`     | `id`, `name`, `orderDate`, `total`, `comment`, `status` (enum), `ownerId` |
| `OrderItem` | `order_item` | `id`, `order` (FK), `idCatalogue`, `quantity`, `subTotal`                  |

### `OrderStatus` (Enum)

```java
EN_PROCESO, CANCELADO, ENTREGADO
```

### Relaciones entre entidades

- **`Order` → `OrderItem`**: Relación **1:N** con `CascadeType.ALL` y `orphanRemoval = true`. Los ítems se persisten y eliminan automáticamente con la orden.
- **`OrderItem` → `Order`**: `@ManyToOne` (Lazy).
- **`OrderItem.idCatalogue`**: Referencia lógica (no FK) al ID del libro en el microservicio de catálogo.

---

## Modelo relacional de base de datos

```
┌──────────────────────────────┐
│           orders             │
├──────────────────────────────┤
│ id          INTEGER (PK)     │──────┐
│ name        VARCHAR(255)     │      │
│ order_date  TIMESTAMP        │      │
│ total       DECIMAL(10,2)    │      │
│ comment     TEXT             │      │
│ status      ENUM             │      │
│   (EN_PROCESO|CANCELADO|     │      │
│    ENTREGADO)                │      │
│ owner_id    INTEGER          │      │
│ created_at  TIMESTAMP        │      │
│ updated_at  TIMESTAMP        │      │
└──────────────────────────────┘      │
                                      │
                                 1:N  │
                                      ▼
                    ┌──────────────────────────────┐
                    │         order_item            │
                    ├──────────────────────────────┤
                    │ id           INTEGER (PK)     │
                    │ order_id     INTEGER (FK)     │
                    │ id_catalogue INTEGER           │  ← Ref. lógica a books-catalogue
                    │ quantity     INTEGER (≥ 0)    │
                    │ sub_total    DECIMAL(10,2)    │
                    └──────────────────────────────┘
```

### Relaciones

- **`orders` → `order_item`**: Relación **1:N**. Cascade `ON DELETE CASCADE`. Cada orden puede tener múltiples ítems.
- **`order_item.id_catalogue`**: Referencia lógica al `book.id` del microservicio de catálogo (no hay FK física, ya que están en bases de datos distintas).

---

## Reconstrucción de la base de datos para pruebas

El script SQL se encuentra en `src/main/resources/db/schema.sql`.

### Implementacion Docker:

```bash
- docker pull mysql
- docker run -p 3306:3306 --name dwfs-book-orders -e MYSQL_ROOT_PASSWORD=mysql -d mysql:latest
```

### Paso 1: Crear el esquema y las tablas

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

Este script:
- Crea el schema `book_orders_db`.
- Crea la tabla `orders` con campo `status` de tipo ENUM.
- Crea la tabla `order_item` con FK a `orders` y constraint CHECK en `quantity`.
- Inserta datos de prueba que cubren los tres estados posibles (`EN_PROCESO`, `CANCELADO`, `ENTREGADO`).

### Reconstrucción desde cero

```bash
mysql -u root -p -e "DROP SCHEMA IF EXISTS book_orders_db;" && \
mysql -u root -p < src/main/resources/db/schema.sql
```

> **Nota**: Los datos de prueba referencian IDs de libros del microservicio **books-catalogue** (`id_catalogue`). Asegúrate de haber ejecutado previamente el script del catálogo para que los IDs referenciados existan.

---

## Configuración

Variables de entorno configurables (`application.yaml`):

| Variable            | Valor por defecto                            | Descripción                                                          |
|---------------------|----------------------------------------------|----------------------------------------------------------------------|
| `DB_URL`            | `jdbc:mysql://localhost:3306/book_orders_db` | URL de conexión JDBC                                                 |
| `DB_DRIVER`         | `com.mysql.cj.jdbc.Driver`                   | Driver JDBC                                                          |
| `DB_USER`           | `root`                                       | Usuario de base de datos                                             |
| `DB_PASSWORD`       | `mysql`                                      | Contraseña de base de datos                                          |
| `EUREKA_URL`        | `http://localhost:8761/eureka`               | URL del servidor Eureka                                              |
| `booksCatalogue.url`| `http://catalogue`                           | URL base del microservicio de catálogo (resuelta vía Eureka)         |
