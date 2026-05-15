package com.unir.book.catalogue.repository;

import com.unir.book.catalogue.entity.BookDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookDescriptionJpaRepository
        extends JpaRepository<BookDescription, Long> {

    boolean existsByIsbn(String isbn);

}