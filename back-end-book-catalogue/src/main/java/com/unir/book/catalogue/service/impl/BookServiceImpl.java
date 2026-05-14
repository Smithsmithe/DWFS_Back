package com.unir.book.catalogue.service.impl;

import com.unir.book.catalogue.dto.BookResponse;
import com.unir.book.catalogue.entity.BookDescription;
import com.unir.book.catalogue.exceptions.ResourceNotFoundException;
import com.unir.book.catalogue.repository.BookDescriptionRepository;
import com.unir.book.catalogue.service.interfaces.BookService;
import com.unir.book.catalogue.utils.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookDescriptionRepository bookDescriptionRepository;

    @Override
    public List<BookResponse> getAllBooks() {

        return bookDescriptionRepository.findAll()
                .stream()
                .map(BookMapper::toResponse)
                .toList();
    }

    @Override
    public BookResponse getBookById(Long id) {

        BookDescription book = bookDescriptionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book not found with id: " + id
                        )
                );

        return BookMapper.toResponse(book);
    }

    @Override
    public BookDescription saveBook(BookDescription bookDescription) {
        return bookDescriptionRepository.save(bookDescription);
    }

    @Override
    public void deleteBook(Long id) {
        bookDescriptionRepository.deleteById(id);
    }

    @Override
    public Page<BookResponse> getBooks(
            String author,
            Pageable pageable
    ) {

        Page<BookDescription> books;

        if (author != null && !author.isBlank()) {

            books = bookDescriptionRepository
                    .findByAuthorContainingIgnoreCase(
                            author,
                            pageable
                    );

        } else {

            books = bookDescriptionRepository.findAll(pageable);
        }

        return books.map(BookMapper::toResponse);
    }

}