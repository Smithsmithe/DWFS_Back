-- =====================================================
-- DDL para crear el esquema de Book Orders
-- =====================================================

CREATE SCHEMA book_orders_db;
USE book_orders_db;

CREATE TABLE orders (
                        id INTEGER NOT NULL AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL,
                        order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        total DECIMAL(10,2) NOT NULL,
                        comment TEXT,
                        status ENUM('EN_PROCESO', 'CANCELADO', 'ENTREGADO') NOT NULL DEFAULT 'EN_PROCESO',
                        owner_id INTEGER NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (id)
);

CREATE TABLE order_item (
                            id INTEGER NOT NULL AUTO_INCREMENT,
                            order_id INTEGER NOT NULL,
                            id_catalogue INTEGER NOT NULL,
                            quantity INTEGER NOT NULL CHECK (quantity >= 0),
                            sub_total DECIMAL(10,2) NOT NULL,
                            PRIMARY KEY (id),
                            CONSTRAINT fk_order_item_order_id
                                FOREIGN KEY (order_id) REFERENCES orders(id)
                                    ON DELETE CASCADE ON UPDATE CASCADE
);

-- =====================================================
-- Datos de prueba
--
-- Consideraciones:
--   · name sigue el patrón de generateOrderName():
--     "ORDER-{System.currentTimeMillis()}"
--     Los timestamps en milisegundos corresponden a las
--     fechas indicadas en order_date.
--   · owner_id = 1 en todas las órdenes, igual que el
--     valor hardcodeado en CreateOrdersService.
--   · id_catalogue referencia el campo `id` de la tabla
--     `book` en book_catalogue_db. El script del catálogo
--     inserta exactamente 100 libros con IDs 1-100,
--     por lo que todos los id_catalogue usados aquí
--     están dentro de ese rango.
--   · Los precios unitarios son valores representativos
--     dentro del rango generado por el catálogo:
--     ROUND(10 + RAND()*80, 2)  →  [10.00, 90.00]
--   · Las quantities son conservadoras (1-2) dado que el
--     stock del catálogo es FLOOR(1 + RAND()*100).
--   · Se cubren los 3 estados del enum para facilitar
--     pruebas de todos los flujos del servicio.
-- =====================================================

INSERT INTO orders (id, name, order_date, total, comment, status, owner_id, created_at, updated_at)
VALUES
-- Orden 1 · ENTREGADA · 2026-05-11 09:00:00
-- 1746954000000 ms = 2026-05-11 09:00:00 UTC
(1, 'ORDER-1746954000000', '2026-05-11 09:00:00', 122.97, 'Primera compra del usuario',  'ENTREGADO',  1, '2026-05-11 09:00:00', '2026-05-13 10:00:00'),

-- Orden 2 · ENTREGADA · 2026-05-12 14:30:00
-- 1747049400000 ms = 2026-05-12 14:30:00 UTC
(2, 'ORDER-1747049400000', '2026-05-12 14:30:00', 79.98,  NULL,                          'ENTREGADO',  1, '2026-05-12 14:30:00', '2026-05-14 08:00:00'),

-- Orden 3 · CANCELADA · 2026-05-13 11:15:00
-- 1747131300000 ms = 2026-05-13 11:15:00 UTC
(3, 'ORDER-1747131300000', '2026-05-13 11:15:00', 45.50,  'Cancelada por el usuario',    'CANCELADO',  1, '2026-05-13 11:15:00', '2026-05-13 16:00:00'),

-- Orden 4 · EN_PROCESO · 2026-05-14 08:45:00
-- 1747212300000 ms = 2026-05-14 08:45:00 UTC
(4, 'ORDER-1747212300000', '2026-05-14 08:45:00', 189.97, NULL,                          'EN_PROCESO', 1, '2026-05-14 08:45:00', '2026-05-14 08:45:00'),

-- Orden 5 · EN_PROCESO · 2026-05-15 17:20:00  (la más reciente)
-- 1747322400000 ms = 2026-05-15 17:20:00 UTC
(5, 'ORDER-1747322400000', '2026-05-15 17:20:00', 54.99,  'Envío urgente solicitado',    'EN_PROCESO', 1, '2026-05-15 17:20:00', '2026-05-15 17:20:00');

-- =====================================================
-- Ítems de las órdenes
--
-- sub_total = precio_unitario × quantity
-- Todos los id_catalogue apuntan a IDs de la tabla `book`
-- del catálogo (rango 1-100 generado por schema_data.sql).
--
-- Precios unitarios de referencia usados:
--   book.id  5 → 29.99  │  book.id 18 → 39.99
--   book.id  8 → 54.99  │  book.id 23 → 15.02
--   book.id 15 → 45.50  │  book.id 37 → 62.00
--   book.id 17 → 19.99  │  book.id 42 → 34.99
--   book.id 67 → 20.98  │  book.id 74 → 55.00
--   book.id 80 → 24.98
--
-- Verificación de totales:
--   Orden 1: 62.00 + 39.99 + 20.98              = 122.97 ✓
--   Orden 2: 55.00 + 24.98                       =  79.98 ✓
--   Orden 3: 45.50                               =  45.50 ✓
--   Orden 4: 54.99 + 69.98 + 29.99 + 19.99 + 15.02 = 189.97 ✓
--   Orden 5: 54.99                               =  54.99 ✓
-- =====================================================

INSERT INTO order_item (id, order_id, id_catalogue, quantity, sub_total)
VALUES
-- Orden 1 → total 122.97
(1,  1, 37, 1, 62.00),   -- book 37 · 1 ud. × 62.00
(2,  1, 18, 1, 39.99),   -- book 18 · 1 ud. × 39.99
(3,  1, 67, 1, 20.98),   -- book 67 · 1 ud. × 20.98

-- Orden 2 → total 79.98
(4,  2, 74, 1, 55.00),   -- book 74 · 1 ud. × 55.00
(5,  2, 80, 1, 24.98),   -- book 80 · 1 ud. × 24.98

-- Orden 3 (CANCELADA) → total 45.50
(6,  3, 15, 1, 45.50),   -- book 15 · 1 ud. × 45.50

-- Orden 4 → total 189.97
(7,  4,  8, 1, 54.99),   -- book  8 · 1 ud. × 54.99
(8,  4, 42, 2, 69.98),   -- book 42 · 2 ud. × 34.99
(9,  4,  5, 1, 29.99),   -- book  5 · 1 ud. × 29.99
(10, 4, 17, 1, 19.99),   -- book 17 · 1 ud. × 19.99
(11, 4, 23, 1, 15.02),   -- book 23 · 1 ud. × 15.02

-- Orden 5 → total 54.99
(12, 5,  8, 1, 54.99);   -- book  8 · 1 ud. × 54.99