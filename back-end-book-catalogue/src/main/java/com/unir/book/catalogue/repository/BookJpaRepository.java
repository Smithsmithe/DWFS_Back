package com.unir.book.catalogue.repository;

import com.unir.book.catalogue.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookJpaRepository extends
        JpaRepository<Book, Long>,
        JpaSpecificationExecutor<Book>,
        PagingAndSortingRepository<Book, Long> {

    /**
     * Listado general con relaciones cargadas
     */
    @Override
    @EntityGraph(attributePaths = {
            "description",
            "description.images"
    })
    List<Book> findAll();

    /**
     * Búsqueda dinámica con specifications
     */
    @Override
    @EntityGraph(attributePaths = {
            "description",
            "description.images"
    })
    List<Book> findAll(Specification<Book> spec);

    /**
     * Búsqueda dinámica paginada
     */
    @Override
    @EntityGraph(attributePaths = {
            "description",
            "description.images"
    })
    Page<Book> findAll(Specification<Book> spec, Pageable pageable);

    /**
     * Paginado simple
     */
    @Override
    @EntityGraph(attributePaths = {
            "description",
            "description.images"
    })
    Page<Book> findAll(Pageable pageable);

    /**
     * Buscar por id con relaciones cargadas
     */
    @Override
    @EntityGraph(attributePaths = {
            "description",
            "description.images"
    })
    Optional<Book> findById(Long id);

    /**
     * Libros visibles
     */
    @Query("""
        SELECT DISTINCT b
        FROM Book b
        JOIN FETCH b.description d
        LEFT JOIN FETCH d.images
        WHERE b.visible = true
        """)
    List<Book> findVisibleBooks();

    /**
     * Libros con stock
     */
    @Query("""
        SELECT DISTINCT b
        FROM Book b
        JOIN FETCH b.description d
        LEFT JOIN FETCH d.images
        WHERE b.stock > 0
        """)
    List<Book> findAvailableBooks();

    /**
     * Libros que están asociados a una descripción
     */
    long countByDescriptionId(Long descriptionId);
}