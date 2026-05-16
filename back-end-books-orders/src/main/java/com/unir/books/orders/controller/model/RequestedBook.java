package com.unir.books.orders.controller.model;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "quantity"
})
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestedBook implements Serializable {

    @Serial
    private final static long serialVersionUID = -2297836609917736443L;

    @JsonProperty("id")
    public Integer id;
    @JsonProperty("quantity")
    public Integer quantity;
}
