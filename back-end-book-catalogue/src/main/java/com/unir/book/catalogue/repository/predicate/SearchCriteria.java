package com.unir.book.catalogue.repository.predicate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SearchCriteria<T> implements Specification<T> {

    private final List<SearchStatement> statements = new ArrayList<>();

    public void add(SearchStatement statement) {
        statements.add(statement);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Predicate toPredicate(
            Root<T> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb
    ) {

        List<Predicate> predicates = new ArrayList<>();

        for (SearchStatement statement : statements) {

            Path<?> path = resolvePath(
                    root,
                    statement.getField().getFieldName()
            );

            switch (statement.getOperation()) {

                case EQUAL ->
                        predicates.add(
                                cb.equal(path, statement.getValue())
                        );

                case MATCH ->
                        predicates.add(
                                cb.like(
                                        cb.lower(path.as(String.class)),
                                        "%" + statement.getValue()
                                                .toString()
                                                .toLowerCase() + "%"
                                )
                        );

                case GREATER_THAN_EQUAL ->
                        predicates.add(
                                cb.greaterThanOrEqualTo(
                                        (Path) path,
                                        (Comparable) statement.getValue()
                                )
                        );

                case LESS_THAN_EQUAL ->
                        predicates.add(
                                cb.lessThanOrEqualTo(
                                        (Path) path,
                                        (Comparable) statement.getValue()
                                )
                        );
            }
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private Path<?> resolvePath(Root<T> root, String fieldPath) {

        String[] parts = fieldPath.split("\\.");

        Path<?> path = root;

        for (String part : parts) {
            path = path.get(part);
        }

        return path;
    }
}