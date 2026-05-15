package com.unir.book.catalogue.utils;

import com.unir.book.catalogue.controller.model.BookResponse;
import com.unir.book.catalogue.entity.BookDescription;

public class BookMapper {

    private BookMapper() {
    }

    public static BookResponse toResponse(
            BookDescription book
    ) {

        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .rating(book.getRating())
                .build();
    }
}