package com.unir.book.catalogue.repository;

import com.unir.book.catalogue.entity.Book;
import com.unir.book.catalogue.repository.predicate.SearchCriteria;
import com.unir.book.catalogue.repository.predicate.SearchFields;
import com.unir.book.catalogue.repository.predicate.SearchOperation;
import com.unir.book.catalogue.repository.predicate.SearchStatement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookCatalogueRepository {

    private final BookJpaRepository bookJpaRepository;

    public List<Book> getBooks(
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
    ) {

        SearchCriteria<Book> spec = new SearchCriteria<>();

        if (StringUtils.hasText(title)) {
            spec.add(new SearchStatement(SearchFields.TITLE, title, SearchOperation.MATCH));
        }

        if (StringUtils.hasText(author)) {
            spec.add(new SearchStatement(SearchFields.AUTHOR, author, SearchOperation.MATCH));
        }

        if (StringUtils.hasText(isbn)) {
            spec.add(new SearchStatement(SearchFields.ISBN, isbn, SearchOperation.EQUAL));
        }

        if (StringUtils.hasText(category)) {
            spec.add(new SearchStatement(SearchFields.CATEGORY, category, SearchOperation.EQUAL));
        }

        if (publicationDate != null) {
            spec.add(new SearchStatement(SearchFields.PUBLICATION_DATE, publicationDate, SearchOperation.EQUAL));
        }

        if (rating != null && rating.compareTo(BigDecimal.ZERO) > 0) {
            spec.add(new SearchStatement(SearchFields.RATING, rating, SearchOperation.GREATER_THAN_EQUAL));
        }

        if (visible != null) {
            spec.add(new SearchStatement(SearchFields.VISIBLE, visible, SearchOperation.EQUAL));
        }

        if (StringUtils.hasText(format)) {
            spec.add(new SearchStatement(SearchFields.FORMAT, format, SearchOperation.EQUAL));
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            spec.add(new SearchStatement(SearchFields.PRICE, maxPrice, SearchOperation.LESS_THAN_EQUAL));
        }

        if (minStock != null && minStock > 0) {
            spec.add(new SearchStatement(SearchFields.STOCK, minStock, SearchOperation.GREATER_THAN_EQUAL));
        }

        return bookJpaRepository.findAll(spec);
    }

    public List<Book> getBooks() {
        return bookJpaRepository.findVisibleBooks();
    }

    public List<Book> getBooks(Integer size, Integer page) {

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException(
                    "Page number must be non-negative and size must be positive."
            );
        }

        return bookJpaRepository
                .findAll(Pageable.ofSize(size).withPage(page))
                .getContent();
    }

    public List<Book> getBooks(
            String title,
            String author,
            String isbn,
            String category,
            LocalDate publicationDate,
            BigDecimal rating,
            Boolean visible,
            String format,
            BigDecimal maxPrice,
            Integer minStock,
            Integer pageSize,
            Integer page
    ) {
        return bookJpaRepository.findAll(
                buildCriteria(
                        title,
                        author,
                        isbn,
                        category,
                        publicationDate,
                        rating,
                        visible,
                        format,
                        maxPrice,
                        minStock
                ),
                Pageable.ofSize(pageSize).withPage(page)
        ).getContent();
    }

    private SearchCriteria<Book> buildCriteria(
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
    ) {
        SearchCriteria<Book> spec = new SearchCriteria<>();

        // mismo bloque de filtros

        return spec;
    }
}