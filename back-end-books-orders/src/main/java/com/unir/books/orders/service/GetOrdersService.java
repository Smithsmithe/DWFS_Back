package com.unir.books.orders.service;

import com.unir.books.orders.controller.model.GetOrdersResponseDto;
import com.unir.books.orders.controller.model.PurchasedItem;
import com.unir.books.orders.controller.model.RecentOrder;
import com.unir.books.orders.facade.BooksCatalogueFacade;
import com.unir.books.orders.facade.model.BookDto;
import com.unir.books.orders.repository.OrderJpaRepository;
import com.unir.books.orders.repository.model.Order;
import com.unir.books.orders.repository.model.OrderItem;
import com.unir.books.orders.repository.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOrdersService {

    private final OrderJpaRepository orderJpaRepository;
    private final BooksCatalogueFacade booksCatalogueFacade;

    @Transactional(readOnly = true)
    public GetOrdersResponseDto getRecentOrders() {
        List<Order> recentOrders = orderJpaRepository.findByOwnerIdOrderByOrderDateDesc(1, Limit.of(5));
        return GetOrdersResponseDto.builder()
                .recentOrders(recentOrders.stream().map(this::getRecentOrder).toList())
                .build();
    }

    @Transactional(readOnly = true)
    public GetOrdersResponseDto getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderJpaRepository.findByStatus(status);
        return GetOrdersResponseDto.builder()
                .recentOrders(orders.stream().map(this::getRecentOrder).toList())
                .build();
    }

    private RecentOrder getRecentOrder(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        List<PurchasedItem> purchasedItems = orderItems.stream().map(this::getBookData).toList();
        return RecentOrder.builder()
                .id(order.getName())
                .status(order.getStatus().name())
                .total(order.getTotal().doubleValue())
                .date(order.getOrderDate().toLocalDate().toString())
                .comment(order.getComment())
                .items(purchasedItems)
                .build();
    }

    private PurchasedItem getBookData(OrderItem orderItem) {
        BookDto book = booksCatalogueFacade.getBook(orderItem.getIdCatalogue());
        return PurchasedItem.builder()
                .title(book.getTitle())
                .price(book.getPrice().doubleValue())
                .quantity(orderItem.getQuantity())
                .build();
    }
}
