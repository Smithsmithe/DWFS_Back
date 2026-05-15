
package com.unir.book.catalogue.repository.predicate;

public enum SearchFields {

    TITLE("description.title"),
    AUTHOR("description.author"),
    ISBN("description.isbn"),
    CATEGORY("description.category"),
    PUBLICATION_DATE("description.publicationDate"),
    RATING("description.rating"),

    VISIBLE("visible"),
    FORMAT("format"),
    PRICE("price"),
    STOCK("stock");

    private final String fieldName;

    SearchFields(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}