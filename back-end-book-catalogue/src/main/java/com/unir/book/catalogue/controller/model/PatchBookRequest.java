package com.unir.book.catalogue.controller.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PatchBookRequest {

    private BigDecimal price;

    private Integer stock;

    private Boolean visible;

    private BigDecimal rating;
}