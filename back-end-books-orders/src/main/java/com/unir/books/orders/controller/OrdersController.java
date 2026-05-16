package com.unir.books.orders.controller;

import com.unir.books.orders.controller.model.CreateOrderRequestDto;
import com.unir.books.orders.controller.model.CreateOrderResponseDto;
import com.unir.books.orders.controller.model.GetOrdersResponseDto;
import com.unir.books.orders.repository.model.OrderStatus;
import com.unir.books.orders.service.CreateOrdersService;
import com.unir.books.orders.service.GetOrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class OrdersController {

    private final CreateOrdersService createOrdersService;
    private final GetOrdersService getOrdersService;

    @GetMapping("orders")
    public ResponseEntity<GetOrdersResponseDto> getRecentOrders(@RequestParam(required = false)OrderStatus status) {

        if (status != null) {
            return  ResponseEntity.ok(getOrdersService.getOrdersByStatus(status));
        }

        return ResponseEntity.ok(getOrdersService.getRecentOrders());
    }

    @PostMapping("orders")
    public ResponseEntity<CreateOrderResponseDto> createOrder(@RequestBody CreateOrderRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createOrdersService.createOrder(request));
    }
}
