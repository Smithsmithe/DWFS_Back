package com.unir.book.catalogue.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class BookResponse {

    private Long id;

    private String title;

    private String author;

    private BigDecimal price;

    private BigDecimal rating;
}