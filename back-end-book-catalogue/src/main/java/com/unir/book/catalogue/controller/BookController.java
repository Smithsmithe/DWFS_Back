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
     * Obtiene el listado completo de libros registrados.
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
     * Realiza una búsqueda dinámica utilizando filtros opcionales.
     */
    @Operation(
            summary = "Buscar libros con filtros",
            description = "Permite consultar libros aplicando criterios dinámicos de búsqueda"
    )
    @GetMapping("/search")
    public List<BookResponse> searchBooks(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "category", required = false) String category,

            @RequestParam(value = "publicationDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate publicationDate,

            @RequestParam(value = "rating", required = false) BigDecimal rating,
            @RequestParam(value = "visible", required = false) Boolean visible,
            @RequestParam(value = "format", required = false) String format,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "minStock", required = false) Integer minStock
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
     * Retorna libros utilizando parámetros de paginación.
     */
    @Operation(
            summary = "Obtener libros paginados",
            description = "Retorna libros utilizando parámetros page y size"
    )
    @GetMapping("/paged")
    public List<BookResponse> getBooksPaginated(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size
    ) {
        return bookService.getBooksPaginated(size, page);
    }

    /**
     * Consulta un libro específico por identificador.
     */
    @Operation(
            summary = "Obtener libro por identificador",
            description = "Retorna un libro específico según su identificador"
    )
    @GetMapping("/{id}")
    public BookResponse getBookById(@PathVariable("id") Long id) {
        return bookService.getBookById(id);
    }

    /**
     * Registra un nuevo libro en el catálogo.
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
     * Actualiza completamente la información de un libro existente.
     */
    @Operation(
            summary = "Actualizar libro",
            description = "Reemplaza completamente la información de un libro existente"
    )
    @PutMapping("/{id}")
    public BookResponse updateBook(
            @PathVariable("id") Long id,
            @RequestBody BookRequest request
    ) {
        return bookService.updateBook(id, request);
    }

    /**
     * Actualiza parcialmente atributos permitidos del libro.
     */
    @Operation(
            summary = "Actualizar parcialmente un libro",
            description = "Modifica atributos específicos sin reemplazar completamente el recurso"
    )
    @PatchMapping("/{id}")
    public BookResponse patchBook(
            @PathVariable("id") Long id,
            @RequestBody PatchBookRequest request
    ) {
        return bookService.patchBook(id, request);
    }

    /**
     * Elimina un libro utilizando su identificador.
     */
    @Operation(
            summary = "Eliminar libro",
            description = "Elimina un libro del catálogo según su identificador"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
    }
}