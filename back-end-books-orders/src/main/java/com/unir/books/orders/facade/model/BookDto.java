package com.unir.books.orders.facade.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookDto {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author")
    private String author;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("stock")
    private Integer stock;
}
