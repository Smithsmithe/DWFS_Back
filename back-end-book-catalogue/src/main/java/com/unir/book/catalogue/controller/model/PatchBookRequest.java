package com.unir.book.catalogue.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Modelo para actualización parcial de atributos operativos")
public class PatchBookRequest {

    @Schema(example = "39.99")
    private BigDecimal price;

    @Schema(example = "20")
    private Integer stock;

    @Schema(example = "false")
    private Boolean visible;

    @Schema(example = "4.7")
    private BigDecimal rating;
}