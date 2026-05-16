package com.unir.books.orders.service;

import com.unir.books.orders.controller.model.CreateOrderRequestDto;
import com.unir.books.orders.controller.model.CreateOrderResponseDto;
import com.unir.books.orders.controller.model.RequestedBook;
import com.unir.books.orders.facade.BooksCatalogueFacade;
import com.unir.books.orders.facade.model.BookDto;
import com.unir.books.orders.exception.BookNotFoundException;
import com.unir.books.orders.repository.OrderJpaRepository;
import com.unir.books.orders.repository.model.Order;
import com.unir.books.orders.repository.model.OrderItem;
import com.unir.books.orders.repository.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class CreateOrdersService {

    private final BooksCatalogueFacade booksCatalogueFacade;
    private final OrderJpaRepository orderJpaRepository;

    @Transactional
    public CreateOrderResponseDto createOrder(CreateOrderRequestDto request) {
        
        if (request.getBooks() == null || request.getBooks().isEmpty()) {
            throw new IllegalArgumentException("La orden debe contener al menos un libro");
        }

        Map<BookDto, OrderItem> bookOrderItemMap = new HashMap<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (RequestedBook book : request.getBooks()) {
            BookDto bookData = getBookData(book);
            OrderItem orderItem = OrderItem.builder()
                    .idCatalogue(book.getId())
                    .quantity(book.getQuantity())
                    .subTotal(getSubTotal(book, bookData))
                    .build();
            totalAmount = totalAmount.add(orderItem.getSubTotal());
            bookOrderItemMap.put(bookData, orderItem);
        }

        String orderName = generateOrderName();
        Order order = Order.builder()
                .name(orderName)
                .orderDate(LocalDateTime.now())
                .total(totalAmount)
                .status(OrderStatus.EN_PROCESO)
                .ownerId(1)
                .orderItems(bookOrderItemMap.values().stream().toList())
                .build();

        bookOrderItemMap.values().forEach(item -> item.setOrder(order));
        Order savedOrder = orderJpaRepository.save(order);

        for (Map.Entry<BookDto, OrderItem> entry : bookOrderItemMap.entrySet()) {
            updateBookStock(entry.getKey().getStock(), entry.getValue());
        }

        return CreateOrderResponseDto.builder()
                .name(savedOrder.getName())
                .build();
    }

    private BookDto getBookData(RequestedBook requestedBook) {
        if (requestedBook.getQuantity() == null || requestedBook.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0 para el libro ID: " + requestedBook.getId());
        }
        return booksCatalogueFacade.getBook(requestedBook.getId());
    }

    private BigDecimal getSubTotal(RequestedBook requestedSupply, BookDto book) {
        if (book == null) {
            throw new BookNotFoundException("Libro no encontrado con ID: " + requestedSupply.getId());
        }
        if (book.getStock() == null || book.getStock() < requestedSupply.getQuantity()) {
            throw new IllegalArgumentException("Stock insuficiente para el libro: " + book.getTitle() +
                    ". Stock disponible: " + book.getStock() + ", solicitado: " + requestedSupply.getQuantity());
        }
        BigDecimal unitPrice = book.getPrice() != null ? book.getPrice() : BigDecimal.ZERO;
        return unitPrice.multiply(BigDecimal.valueOf(requestedSupply.getQuantity()));
    }

    private String generateOrderName() {
        return "ORDER-" + System.currentTimeMillis();
    }

    private void updateBookStock(Integer currentStock, OrderItem item) {
        int newStock = currentStock - item.getQuantity();
        if (newStock < 0) {
            throw new IllegalArgumentException("Error crítico: el stock resultante sería negativo para el libro ID: " + item.getId());
        }
        booksCatalogueFacade.updateBookStock(item.getIdCatalogue(), newStock);
    }
}