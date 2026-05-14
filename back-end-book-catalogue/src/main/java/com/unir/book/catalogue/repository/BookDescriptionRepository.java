package com.unir.book.catalogue.repository;

import com.unir.book.catalogue.entity.BookDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BookDescriptionRepository extends JpaRepository<BookDescription, Long> {
    Page<BookDescription> findByAuthorContainingIgnoreCase(
            String author,
            Pageable pageable
    );
}

