package com.unir.books.orders.facade;

import com.unir.books.orders.exception.BadBookModificationException;
import com.unir.books.orders.exception.BookNotFoundException;
import com.unir.books.orders.exception.InternalErrorException;
import com.unir.books.orders.facade.model.BookDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import java.util.Map;

@Component
public class BooksCatalogueFacade {

    private final RestClient.Builder restClientBuilder;

    @Value("${booksCatalogue.url}")
    private String booksCatalogueUrl;

    public BooksCatalogueFacade(@Qualifier("loadBalancedRestClient") RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    public BookDto getBook(Integer bookId) {
        try {
            return restClientBuilder.build()
                    .get()
                    .uri(booksCatalogueUrl + "/api/books/v1/{id}", bookId)
                    .retrieve()
                    .body(BookDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found", e);
        } catch (HttpServerErrorException.InternalServerError e) {
            throw new InternalErrorException("An exception occurred fetching book with ID " + bookId, e);
        }
    }

    public void updateBookStock(Integer bookId, Integer stock) {
        try {
            restClientBuilder.build()
                    .patch()
                    .uri(booksCatalogueUrl + "/api/books/v1/{id}", bookId)
                    .body(Map.of("stock", stock))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found", e);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new BadBookModificationException("Bad request when updating stock for book with ID " + bookId, e);
        } catch (HttpServerErrorException.InternalServerError e) {
            throw new InternalErrorException("An exception occurred updating stock for book with ID " + bookId, e);
        }
    }
}