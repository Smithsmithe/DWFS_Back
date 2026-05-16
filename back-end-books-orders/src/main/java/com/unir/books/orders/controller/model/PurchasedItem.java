package com.unir.books.orders.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "title",
        "quantity",
        "price"
})
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PurchasedItem implements Serializable {

    @Serial
    private final static long serialVersionUID = -4761762119375139021L;

    @JsonProperty("title")
    public String title;
    @JsonProperty("quantity")
    public Integer quantity;
    @JsonProperty("price")
    public Double price;

}
