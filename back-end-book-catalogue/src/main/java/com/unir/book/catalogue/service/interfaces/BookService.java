package com.unir.book.catalogue.service.interfaces;

import com.unir.book.catalogue.dto.BookResponse;
import com.unir.book.catalogue.entity.BookDescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {

    List<BookResponse> getAllBooks();

    BookResponse getBookById(Long id);

    BookDescription saveBook(BookDescription bookDescription);

    void deleteBook(Long id);

    Page<BookResponse> getBooks(
            String author,
            Pageable pageable
    );
}