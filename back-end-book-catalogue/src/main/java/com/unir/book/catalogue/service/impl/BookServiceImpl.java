package com.unir.book.catalogue.service.impl;

import com.unir.book.catalogue.controller.model.BookRequest;
import com.unir.book.catalogue.controller.model.BookResponse;
import com.unir.book.catalogue.controller.model.PatchBookRequest;
import com.unir.book.catalogue.entity.Book;
import com.unir.book.catalogue.entity.BookDescription;
import com.unir.book.catalogue.entity.BookImage;
import com.unir.book.catalogue.exceptions.DuplicateResourceException;
import com.unir.book.catalogue.exceptions.ResourceNotFoundException;
import com.unir.book.catalogue.repository.BookCatalogueRepository;
import com.unir.book.catalogue.repository.BookDescriptionJpaRepository;
import com.unir.book.catalogue.repository.BookImageJpaRepository;
import com.unir.book.catalogue.repository.BookJpaRepository;
import com.unir.book.catalogue.service.interfaces.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookCatalogueRepository bookCatalogueRepository;
    private final BookJpaRepository bookJpaRepository;
    private final BookDescriptionJpaRepository bookDescriptionJpaRepository;
    private final BookImageJpaRepository bookImageJpaRepository;

    /**
     * Realiza búsqueda dinámica de libros usando filtros opcionales.
     */
    @Override
    public List<BookResponse> searchBooks(
            String title,
            String author,
            String isbn,
            String category,
            LocalDate publicationDate,
            BigDecimal rating,
            Boolean visible,
            String format,
            BigDecimal maxPrice,
            Integer minStock
    ) {
        return bookCatalogueRepository.getBooks(
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
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Obtiene todos los libros del catálogo.
     */
    @Override
    public List<BookResponse> getAllBooks() {
        return bookCatalogueRepository.getBooks()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Obtiene libros paginados.
     */
    @Override
    public List<BookResponse> getBooksPaginated(Integer pageSize, Integer page) {
        return bookCatalogueRepository.getBooks(pageSize, page)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Obtiene un libro por identificador.
     */
    @Override
    public BookResponse getBookById(Long id) {
        return mapToResponse(findBookOrThrow(id));
    }

    /**
     * Crea un nuevo libro con su descripción e imágenes.
     */
    @Override
    public BookResponse createBook(BookRequest request) {

        validateDuplicateIsbn(request.getIsbn());

        BookDescription description = BookDescription.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .shortDescription(request.getShortDescription())
                .fullDescription(request.getFullDescription())
                .isbn(request.getIsbn())
                .category(request.getCategory())
                .publicationDate(request.getPublicationDate())
                .language(request.getLanguage())
                .editorial(request.getEditorial())
                .pages(request.getPages())
                .rating(request.getRating())
                .build();

        BookDescription savedDescription =
                bookDescriptionJpaRepository.save(description);

        saveImages(savedDescription, request.getImageUrls());

        Book book = Book.builder()
                .description(savedDescription)
                .format(request.getFormat())
                .price(request.getPrice())
                .stock(request.getStock())
                .visible(request.getVisible())
                .build();

        Book savedBook = bookJpaRepository.save(book);

        return mapToResponse(reloadBook(savedBook.getId()));
    }

    /**
     * Actualiza completamente un libro existente.
     */
    @Override
    public BookResponse updateBook(Long id, BookRequest request) {

        Book book = findBookOrThrow(id);
        BookDescription description = book.getDescription();

        if (!description.getIsbn().equals(request.getIsbn())) {
            validateDuplicateIsbn(request.getIsbn());
        }

        updateDescription(description, request);

        book.setFormat(request.getFormat());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setVisible(request.getVisible());

        bookDescriptionJpaRepository.save(description);

        if (request.getImageUrls() != null) {

            if (description.getImages() != null &&
                    !description.getImages().isEmpty()) {
                bookImageJpaRepository.deleteAll(description.getImages());
            }

            saveImages(description, request.getImageUrls());
        }

        Book updatedBook = bookJpaRepository.save(book);

        return mapToResponse(reloadBook(updatedBook.getId()));
    }

    /**
     * Actualiza parcialmente un libro existente.
     */
    @Override
    public BookResponse patchBook(Long id, PatchBookRequest request) {

        Book book = findBookOrThrow(id);
        BookDescription description = book.getDescription();

        if (request.getPrice() != null) {
            book.setPrice(request.getPrice());
        }

        if (request.getStock() != null) {
            book.setStock(request.getStock());
        }

        if (request.getVisible() != null) {
            book.setVisible(request.getVisible());
        }

        if (request.getRating() != null) {
            description.setRating(request.getRating());
        }

        bookDescriptionJpaRepository.save(description);
        Book updatedBook = bookJpaRepository.save(book);

        return mapToResponse(reloadBook(updatedBook.getId()));
    }

    /**
     * Elimina un libro y, si corresponde, su descripción asociada.
     */
    @Override
    public void deleteBook(Long id) {

        Book book = findBookOrThrow(id);
        Long descriptionId = book.getDescription().getId();

        long relatedBooks = bookJpaRepository.countByDescriptionId(descriptionId);

        bookJpaRepository.delete(book);

        if (relatedBooks <= 1) {

            BookDescription description =
                    bookDescriptionJpaRepository.findById(descriptionId)
                            .orElse(null);

            if (description != null) {
                bookDescriptionJpaRepository.delete(description);
            }
        }
    }

    /**
     * Busca un libro o lanza excepción si no existe.
     */
    private Book findBookOrThrow(Long id) {
        return bookJpaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found"));
    }

    /**
     * Recarga el libro con relaciones completas.
     */
    private Book reloadBook(Long id) {
        return findBookOrThrow(id);
    }

    /**
     * Valida si el ISBN ya existe en el sistema.
     */
    private void validateDuplicateIsbn(String isbn) {

        if (bookDescriptionJpaRepository.existsByIsbn(isbn)) {
            throw new DuplicateResourceException(
                    "A book with ISBN " + isbn + " already exists"
            );
        }
    }

    /**
     * Guarda imágenes asociadas a una descripción.
     * La primera imagen se marca como principal.
     */
    private void saveImages(
            BookDescription description,
            List<String> imageUrls
    ) {

        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        for (int i = 0; i < imageUrls.size(); i++) {

            BookImage image = BookImage.builder()
                    .description(description)
                    .imageUrl(imageUrls.get(i))
                    .isMain(i == 0)
                    .build();

            bookImageJpaRepository.save(image);
        }
    }

    /**
     * Actualiza datos descriptivos del libro.
     */
    private void updateDescription(
            BookDescription description,
            BookRequest request
    ) {
        description.setTitle(request.getTitle());
        description.setAuthor(request.getAuthor());
        description.setShortDescription(request.getShortDescription());
        description.setFullDescription(request.getFullDescription());
        description.setIsbn(request.getIsbn());
        description.setCategory(request.getCategory());
        description.setPublicationDate(request.getPublicationDate());
        description.setLanguage(request.getLanguage());
        description.setEditorial(request.getEditorial());
        description.setPages(request.getPages());
        description.setRating(request.getRating());
    }

    /**
     * Convierte entidad Book a modelo de respuesta.
     */
    private BookResponse mapToResponse(Book book) {

        if (book == null || book.getDescription() == null) {
            throw new ResourceNotFoundException("Invalid book data");
        }

        return BookResponse.builder()
                .id(book.getId())
                .title(book.getDescription().getTitle())
                .author(book.getDescription().getAuthor())
                .isbn(book.getDescription().getIsbn())
                .category(book.getDescription().getCategory())
                .publicationDate(book.getDescription().getPublicationDate())
                .rating(book.getDescription().getRating())
                .format(book.getFormat())
                .price(book.getPrice())
                .stock(book.getStock())
                .visible(book.getVisible())
                .mainImage(getMainImage(book))
                .build();
    }

    /**
     * Obtiene la imagen principal del libro.
     */
    private String getMainImage(Book book) {

        if (book.getDescription().getImages() == null) {
            return null;
        }

        return book.getDescription()
                .getImages()
                .stream()
                .filter(BookImage::getIsMain)
                .map(BookImage::getImageUrl)
                .findFirst()
                .orElse(null);
    }
}