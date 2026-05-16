CREATE DATABASE book_catalogue_db;
USE book_catalogue_db;

-- =========================================
-- TABLE: BOOK DESCRIPTION
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
-- TABLE: BOOK
-- =========================================

CREATE TABLE book
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    description_id BIGINT NOT NULL,

    format VARCHAR(50) NOT NULL,
    price DECIMAL(10,2),
    stock INT NOT NULL,
    visible BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_book_description
        FOREIGN KEY (description_id)
            REFERENCES book_description(id)
            ON DELETE CASCADE
);

-- =========================================
-- TABLE: BOOK IMAGE
-- =========================================

CREATE TABLE book_image
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    description_id BIGINT NOT NULL,

    image_url VARCHAR(500),
    is_main BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_book_image_description
        FOREIGN KEY (description_id)
            REFERENCES book_description(id)
            ON DELETE CASCADE
);

-- =========================================
-- GENERATE 100 REALISTIC DESCRIPTIONS
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
                       SELECT 1 AS n

                       UNION ALL

                       SELECT n + 1
                       FROM numbers
                       WHERE n < 100
                   )

SELECT
    CONCAT(
            ELT(
                    1 + FLOOR(RAND()*15),
                    'The Silent',
                    'Hidden',
                    'Echoes of',
                    'Beyond',
                    'Shadow of',
                    'Rise of',
                    'Whispers of',
                    'Legacy of',
                    'Fragments of',
                    'Chronicles of',
                    'Dreams of',
                    'Return to',
                    'Secrets of',
                    'Voices from',
                    'Last Days of'
            ),
            ' ',
            ELT(
                    1 + FLOOR(RAND()*15),
                    'the Forest',
                    'Tomorrow',
                    'the Kingdom',
                    'Infinity',
                    'Lost Memories',
                    'the Horizon',
                    'Ancient Fire',
                    'the Forgotten City',
                    'Dark Waters',
                    'the Last Empire',
                    'Silent Truth',
                    'Broken Dreams',
                    'the Stars',
                    'the Unknown',
                    'Winter'
            )
    ),

    CONCAT(
            ELT(
                    1 + FLOOR(RAND()*15),
                    'Emily',
                    'Daniel',
                    'Sophia',
                    'Michael',
                    'Laura',
                    'David',
                    'Isabella',
                    'James',
                    'Olivia',
                    'William',
                    'Charlotte',
                    'Alexander',
                    'Emma',
                    'Noah',
                    'Lucas'
            ),
            ' ',
            ELT(
                    1 + FLOOR(RAND()*15),
                    'Carter',
                    'Wilson',
                    'Miller',
                    'Johnson',
                    'Brown',
                    'Taylor',
                    'Anderson',
                    'Thomas',
                    'Moore',
                    'Jackson',
                    'Martin',
                    'White',
                    'Harris',
                    'Walker',
                    'Scott'
            )
    ),

    ELT(
            1 + FLOOR(RAND()*10),
            'A compelling journey through mystery and discovery.',
            'An unforgettable story of resilience and transformation.',
            'A thrilling exploration of hidden truths and unexpected alliances.',
            'A heartfelt narrative about ambition, sacrifice, and redemption.',
            'An immersive tale blending suspense, emotion, and adventure.',
            'A fascinating perspective on technology and the future of society.',
            'A dramatic encounter between destiny and free will.',
            'A captivating story where every choice changes the outcome.',
            'A remarkable journey through imagination and conflict.',
            'An inspiring narrative filled with secrets and emotional depth.'
    ),

    ELT(
            1 + FLOOR(RAND()*10),
            'Set against a richly detailed backdrop, this novel follows characters confronting impossible decisions, personal loss, and transformative discoveries that challenge everything they believed to be true.',
            'A layered narrative that combines emotional storytelling with compelling suspense, offering readers a memorable experience full of unexpected revelations and meaningful character development.',
            'Through vivid storytelling and strong emotional depth, this book explores ambition, morality, innovation, and the human cost of pursuing dreams in uncertain times.',
            'An immersive literary experience where mystery, courage, and personal transformation intersect in a story designed to captivate readers from beginning to end.',
            'This compelling work presents a thoughtful journey through conflict, discovery, and redemption, with memorable characters navigating a world filled with uncertainty and possibility.',
            'A sophisticated narrative blending adventure, emotional realism, and thought-provoking themes that invite reflection on identity, progress, and resilience.',
            'An engaging story built around conflict, emotional complexity, and unexpected alliances, offering a rewarding experience for readers seeking depth and entertainment.',
            'This book delivers a powerful exploration of hope, adversity, and human connection through a carefully crafted narrative full of meaningful moments.',
            'A captivating modern story that combines imagination, strategic conflict, and emotional nuance to create a highly immersive reading experience.',
            'A compelling fictional journey that examines trust, ambition, and the consequences of difficult choices in a rapidly changing world.'
    ),

    CONCAT('978', LPAD(100000000 + n, 10, '0')),

    ELT(
            1 + FLOOR(RAND()*8),
            'Fantasy',
            'Drama',
            'Romance',
            'Mystery',
            'Science Fiction',
            'Technology',
            'Adventure',
            'Thriller'
    ),

    DATE_ADD('2014-01-01', INTERVAL FLOOR(RAND()*4000) DAY),

    ELT(
            1 + FLOOR(RAND()*4),
            'English',
            'Spanish',
            'French',
            'German'
    ),

    ELT(
            1 + FLOOR(RAND()*8),
            'Penguin Random House',
            'HarperCollins',
            'O Reilly Media',
            'Vintage Books',
            'Simon & Schuster',
            'Macmillan Publishers',
            'Oxford Press',
            'Pearson Publishing'
    ),

    FLOOR(180 + RAND()*650),

    ROUND(3.5 + RAND()*1.5, 1)

FROM numbers;

-- =========================================
-- EXACTLY 100 BOOKS
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
            1 + FLOOR(RAND()*4),
            'Hardcover',
            'Paperback',
            'Digital',
            'Collector Edition'
    ),

    ROUND(10 + RAND()*80, 2),

    FLOOR(1 + RAND()*100),

    TRUE

FROM book_description;

-- =========================================
-- MAIN IMAGE
-- =========================================

INSERT INTO book_image
(
    description_id,
    image_url,
    is_main
)
SELECT
    id,
    CONCAT('https://images.books.dev/book-', id, '-main.jpg'),
    TRUE
FROM book_description;

-- =========================================
-- SECONDARY IMAGE
-- =========================================

INSERT INTO book_image
(
    description_id,
    image_url,
    is_main
)
SELECT
    id,
    CONCAT('https://images.books.dev/book-', id, '-secondary.jpg'),
    FALSE
FROM book_description;

