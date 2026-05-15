package com.unir.book.catalogue.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Schema(description = "Modelo para creación o actualización completa de libros")
public class BookRequest {

    @Schema(example = "Clean Code")
    private String title;

    @Schema(example = "Robert C. Martin")
    private String author;

    @Schema(example = "Software engineering best practices")
    private String shortDescription;

    @Schema(example = "A handbook of agile software craftsmanship.")
    private String fullDescription;

    @Schema(example = "9780132350884")
    private String isbn;

    @Schema(example = "Software")
    private String category;

    @Schema(example = "2008-08-01")
    private LocalDate publicationDate;

    @Schema(example = "English")
    private String language;

    @Schema(example = "Prentice Hall")
    private String editorial;

    @Schema(example = "464")
    private Integer pages;

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

    @Schema(
            example = "[\"https://example.com/clean-code-1.jpg\", \"https://example.com/clean-code-2.jpg\"]"
    )
    private List<String> imageUrls;
}