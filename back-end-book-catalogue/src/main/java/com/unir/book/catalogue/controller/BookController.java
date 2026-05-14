package com.unir.book.catalogue.controller;

import com.unir.book.catalogue.dto.BookResponse;
import com.unir.book.catalogue.entity.BookDescription;
import com.unir.book.catalogue.service.interfaces.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {

        return ResponseEntity.ok(
                bookService.getAllBooks()
        );
    }

    @PostMapping
    public ResponseEntity<BookDescription> saveBook(
            @RequestBody BookDescription bookDescription
    ) {

        BookDescription savedBook = bookService.saveBook(bookDescription);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedBook);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                bookService.getBookById(id)
        );
    }
    @GetMapping("/search")
    public ResponseEntity<Page<BookResponse>> searchBooks(

            @RequestParam(required = false)
            String author,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                bookService.getBooks(author, pageable)
        );
    }
}