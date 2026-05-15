package com.unir.book.catalogue.controller;

import com.unir.book.catalogue.controller.model.BookRequest;
import com.unir.book.catalogue.controller.model.BookResponse;
import com.unir.book.catalogue.controller.model.PatchBookRequest;
import com.unir.book.catalogue.service.interfaces.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/books/v1")
@RequiredArgsConstructor
@Tag(
        name = "Catálogo de Libros",
        description = "Operaciones REST para la gestión del catálogo de libros"
)
public class BookController {

    private final BookService bookService;

    /**
     * Obtiene todos los libros del catálogo.
     */
    @Operation(
            summary = "Obtener todos los libros",
            description = "Retorna el listado completo de libros disponibles en el catálogo"
    )
    @GetMapping
    public List<BookResponse> getAllBooks() {
        return bookService.getAllBooks();
    }

    /**
     * Realiza búsqueda dinámica aplicando filtros opcionales.
     */
    @Operation(
            summary = "Buscar libros con filtros",
            description = "Permite consultar libros aplicando filtros dinámicos"
    )
    @GetMapping("/search")
    public List<BookResponse> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String category,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate publicationDate,

            @RequestParam(required = false) BigDecimal rating,
            @RequestParam(required = false) Boolean visible,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minStock
    ) {
        return bookService.searchBooks(
                title,
                author,
                isbn,
                category,
                publicationDate,
                rating,
                visible,
                format,
                maxPrice,
                minStock
        );
    }

    /**
     * Obtiene libros paginados.
     */
    @Operation(
            summary = "Obtener libros paginados",
            description = "Retorna libros utilizando parámetros de paginación"
    )
    @GetMapping("/paged")
    public List<BookResponse> getBooksPaginated(
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        return bookService.getBooksPaginated(size, page);
    }

    /**
     * Obtiene un libro por su identificador.
     */
    @Operation(
            summary = "Obtener libro por identificador",
            description = "Retorna un libro específico según su identificador"
    )
    @GetMapping("/{id}")
    public BookResponse getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    /**
     * Crea un nuevo libro en el catálogo.
     */
    @Operation(
            summary = "Crear libro",
            description = "Registra un nuevo libro con su descripción e imágenes asociadas"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(
            @RequestBody BookRequest request
    ) {
        return bookService.createBook(request);
    }

    /**
     * Actualiza completamente un libro existente.
     */
    @Operation(
            summary = "Actualizar libro",
            description = "Reemplaza completamente la información de un libro existente"
    )
    @PutMapping("/{id}")
    public BookResponse updateBook(
            @PathVariable Long id,
            @RequestBody BookRequest request
    ) {
        return bookService.updateBook(id, request);
    }

    /**
     * Actualiza parcialmente un libro existente.
     */
    @Operation(
            summary = "Actualizar parcialmente un libro",
            description = "Modifica atributos específicos sin reemplazar el recurso completo"
    )
    @PatchMapping("/{id}")
    public BookResponse patchBook(
            @PathVariable Long id,
            @RequestBody PatchBookRequest request
    ) {
        return bookService.patchBook(id, request);
    }

    /**
     * Elimina un libro del catálogo.
     */
    @Operation(
            summary = "Eliminar libro",
            description = "Elimina un libro utilizando su identificador"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}