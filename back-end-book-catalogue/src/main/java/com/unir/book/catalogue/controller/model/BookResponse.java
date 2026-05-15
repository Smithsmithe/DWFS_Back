package com.unir.book.catalogue.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@Schema(description = "Modelo de respuesta del catálogo")
public class BookResponse {

    @Schema(example = "109")
    private Long id;

    @Schema(example = "Clean Code")
    private String title;

    @Schema(example = "Robert C. Martin")
    private String author;

    @Schema(example = "9780132350884")
    private String isbn;

    @Schema(example = "Software")
    private String category;

    @Schema(example = "2008-08-01")
    private LocalDate publicationDate;

    @Schema(example = "4.8")
    private BigDecimal rating;

    @Schema(example = "Paperback")
    private String format;

    @Schema(example = "59.99")
    private BigDecimal price;

    @Schema(example = "10")
    private Integer stock;

    @Schema(example = "true")
    private Boolean visible;

    @Schema(example = "https://example.com/clean-code-1.jpg")
    private String mainImage;
}