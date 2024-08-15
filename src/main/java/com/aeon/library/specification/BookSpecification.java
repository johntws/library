package com.aeon.library.specification;

import com.aeon.library.entity.Book;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    public static Specification<Book> likeIsbn(String isbn) {
        return (root, query, criteriaBuilder) -> {
            if (Strings.isBlank(isbn)) {
                return null;
            }

            return criteriaBuilder.like(root.get("isbn"), "%" + isbn + "%");
        };
    }

    public static Specification<Book> likeTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (Strings.isBlank(title)) {
                return null;
            }

            return criteriaBuilder.like(root.get("title"), "%" + title + "%");
        };
    }

    public static Specification<Book> likeAuthor(String author) {
        return (root, query, criteriaBuilder) -> {
            if (Strings.isBlank(author)) {
                return null;
            }

            return criteriaBuilder.like(root.get("author"), "%" + author + "%");
        };
    }

    public static Specification<Book> equalDeleted(Boolean deleted) {
        return (root, query, criteriaBuilder) -> {
            if (deleted == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("deleted"), deleted);
        };
    }
}
