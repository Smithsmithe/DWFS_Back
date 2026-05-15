package com.unir.book.catalogue.service.interfaces;

import com.unir.book.catalogue.controller.model.BookRequest;
import com.unir.book.catalogue.controller.model.BookResponse;
import com.unir.book.catalogue.controller.model.PatchBookRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BookService {

    List<BookResponse> searchBooks(
            String title,
            String author,
            String isbn,
            String category,
            LocalDate publicationDate,
            BigDecimal rating,
            Boolean visible,
            String format,
            BigDecimal maxPrice,
            Integer minStock
    );

    List<BookResponse> getAllBooks();

    List<BookResponse> getBooksPaginated(Integer pageSize, Integer page);

    BookResponse getBookById(Long id);

    BookResponse createBook(BookRequest request);

    BookResponse updateBook(Long id, BookRequest request);

    BookResponse patchBook(Long id, PatchBookRequest request);

    void deleteBook(Long id);
}