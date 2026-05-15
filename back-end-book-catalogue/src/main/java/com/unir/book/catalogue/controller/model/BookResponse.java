package com.unir.book.catalogue.controller.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class BookResponse {

    private Long id;

    private String title;

    private String author;

    private String isbn;

    private String category;

    private LocalDate publicationDate;

    private BigDecimal rating;

    private String format;

    private BigDecimal price;

    private Integer stock;

    private Boolean visible;

    private String mainImage;
}