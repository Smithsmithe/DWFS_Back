package com.unir.book.catalogue.controller.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class BookRequest {

    private String title;

    private String author;

    private String shortDescription;

    private String fullDescription;

    private String isbn;

    private String category;

    private LocalDate publicationDate;

    private String language;

    private String editorial;

    private Integer pages;

    private BigDecimal rating;

    private String format;

    private BigDecimal price;

    private Integer stock;

    private Boolean visible;

    private List<String> imageUrls;
}