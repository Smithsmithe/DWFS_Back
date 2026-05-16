package com.unir.books.orders.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "id_catalogue", nullable = false)
    private Integer idCatalogue;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "sub_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal subTotal;
}
