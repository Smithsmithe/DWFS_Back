-- =========================================
-- DATABASE
-- =========================================

CREATE DATABASE book_catalogue_db;

USE book_catalogue_db;

-- =========================================
-- TABLE: book_description
-- =========================================

CREATE TABLE book_description
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    title VARCHAR(255) NOT NULL,

    author VARCHAR(255) NOT NULL,

    short_description VARCHAR(500),

    full_description TEXT,

    isbn VARCHAR(20) UNIQUE NOT NULL,

    category VARCHAR(100),

    publication_date DATE,

    language VARCHAR(50),

    editorial VARCHAR(100),

    pages INT,

    rating DECIMAL(2,1) DEFAULT 0.0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);

-- =========================================
-- TABLE: book
-- =========================================

CREATE TABLE book
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    description_id BIGINT NOT NULL,

    format VARCHAR(50),

    price DECIMAL(10,2),

    stock INT DEFAULT 0,

    visible BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_book_description_id
        FOREIGN KEY (description_id)
            REFERENCES book_description(id)
            ON DELETE CASCADE
);

-- =========================================
-- TABLE: book_image
-- =========================================

CREATE TABLE book_image
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    description_id BIGINT NOT NULL,

    image_url VARCHAR(500) NOT NULL,

    is_main BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_book_image_description_id
        FOREIGN KEY (description_id)
            REFERENCES book_description(id)
            ON DELETE CASCADE
);

-- =========================================
-- SAMPLE DATA: BOOK DESCRIPTIONS
-- =========================================

INSERT INTO book_description
(
    title,
    author,
    short_description,
    full_description,
    isbn,
    category,
    publication_date,
    language,
    editorial,
    pages,
    rating
)
VALUES
    (
        'The Silent Forest',
        'Emily Carter',
        'A mystery hidden deep in the woods.',
        'An investigative journalist uncovers secrets buried for decades inside an isolated forest community.',
        '9780000000001',
        'Mystery',
        '2018-02-11',
        'English',
        'Penguin Random House',
        320,
        4.5
    ),
    (
        'Digital Horizon',
        'Sophia Miller',
        'Technology shaping humanity.',
        'A fascinating exploration about artificial intelligence and the ethical challenges of future societies.',
        '9780000000002',
        'Technology',
        '2021-09-10',
        'English',
        'O Reilly Media',
        410,
        4.8
    ),
    (
        'Hidden Kingdom',
        'Daniel Wilson',
        'Fantasy and ancient magic.',
        'A forgotten kingdom rises again beneath the mountains while a young warrior discovers his destiny.',
        '9780000000003',
        'Fantasy',
        '2017-03-15',
        'Spanish',
        'HarperCollins',
        510,
        4.7
    );

-- =========================================
-- SAMPLE DATA: BOOK VARIANTS
-- =========================================

INSERT INTO book
(
    description_id,
    format,
    price,
    stock,
    visible
)
VALUES
    (1, 'Hardcover', 24.99, 20, true),
    (1, 'Paperback', 18.50, 35, true),

    (2, 'Hardcover', 32.00, 15, true),
    (2, 'Digital', 12.99, 999, true),

    (3, 'Paperback', 21.40, 12, true),
    (3, 'Collector Edition', 45.00, 5, true);

-- =========================================
-- SAMPLE DATA: BOOK IMAGES
-- =========================================

INSERT INTO book_image
(
    description_id,
    image_url,
    is_main
)
VALUES
    (
        1,
        'https://images.books.dev/the-silent-forest-main.jpg',
        true
    ),
    (
        1,
        'https://images.books.dev/the-silent-forest-back.jpg',
        false
    ),
    (
        2,
        'https://images.books.dev/digital-horizon-main.jpg',
        true
    ),
    (
        3,
        'https://images.books.dev/hidden-kingdom-main.jpg',
        true
    );

-- =========================================
-- GENERATE MORE SAMPLE BOOKS (4 - 100)
-- =========================================

INSERT INTO book_description
(
    title,
    author,
    short_description,
    full_description,
    isbn,
    category,
    publication_date,
    language,
    editorial,
    pages,
    rating
)
WITH RECURSIVE numbers AS
                   (
                       SELECT 4 AS n

                       UNION ALL

                       SELECT n + 1
                       FROM numbers
                       WHERE n < 100
                   )

SELECT
    CONCAT('Book Title ', n),
    CONCAT('Author ', n),
    CONCAT('Short description for book ', n),
    CONCAT(
            'Full description for book ',
            n,
            '. An engaging and immersive story with compelling characters and emotional depth.'
    ),
    CONCAT('9780000000', LPAD(n, 3, '0')),

    ELT(
            1 + FLOOR(RAND() * 8),
            'Fantasy',
            'Drama',
            'Romance',
            'Mystery',
            'Science Fiction',
            'Technology',
            'Adventure',
            'Thriller'
    ),

    DATE_ADD('2015-01-01', INTERVAL FLOOR(RAND() * 3000) DAY),

    ELT(
            1 + FLOOR(RAND() * 4),
            'English',
            'Spanish',
            'French',
            'German'
    ),

    ELT(
            1 + FLOOR(RAND() * 4),
            'Penguin Random House',
            'HarperCollins',
            'O Reilly Media',
            'Vintage Books'
    ),

    FLOOR(150 + (RAND() * 500)),

    ROUND(3 + (RAND() * 2), 1)

FROM numbers;

-- =========================================
-- GENERATE BOOK VARIANTS
-- =========================================

INSERT INTO book
(
    description_id,
    format,
    price,
    stock,
    visible
)

SELECT
    id,

    ELT(
            1 + FLOOR(RAND() * 4),
            'Hardcover',
            'Paperback',
            'Digital',
            'Collector Edition'
    ),

    ROUND(10 + (RAND() * 50), 2),

    FLOOR(1 + (RAND() * 100)),

    true

FROM book_description
WHERE id BETWEEN 4 AND 100;

-- =========================================
-- GENERATE BOOK IMAGES
-- =========================================

INSERT INTO book_image
(
    description_id,
    image_url,
    is_main
)

SELECT
    id,

    CONCAT(
            'https://images.books.dev/book-',
            id,
            '.jpg'
    ),

    true

FROM book_description
WHERE id BETWEEN 4 AND 100;