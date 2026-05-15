package com.unir.book.catalogue.repository.predicate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchStatement {

    private SearchFields field;
    private Object value;
    private SearchOperation operation;
}