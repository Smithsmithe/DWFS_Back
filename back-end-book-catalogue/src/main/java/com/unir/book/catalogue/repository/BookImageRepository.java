package com.unir.book.catalogue.repository;

import com.unir.book.catalogue.entity.BookImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookImageRepository extends JpaRepository<BookImage, Long> {

}