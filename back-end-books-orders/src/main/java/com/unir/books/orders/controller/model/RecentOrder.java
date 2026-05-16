package com.unir.books.orders.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "date",
        "status",
        "total",
        "comment",
        "items"
})
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecentOrder implements Serializable {

    @Serial
    private final static long serialVersionUID = 2923892800706299020L;

    @JsonProperty("id")
    private String id;
    @JsonProperty("date")
    private String date;
    @JsonProperty("status")
    private String status;
    @JsonProperty("total")
    private Double total;
    @JsonProperty("comment")
    private String comment;
    @JsonProperty("items")
    private List<PurchasedItem> items;

}
