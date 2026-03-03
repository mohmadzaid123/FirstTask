package org.example.library.exceptions;

import org.example.library.domain.Book;
import org.example.library.domain.Member;

public class ItemAlreadyBorrowedException extends RuntimeException {
    public ItemAlreadyBorrowedException(Book book, Member borrowedBy) {
        super("Book '" + book.getTitle() + "' is already borrowed by " +
                (borrowedBy == null ? "unknown" : borrowedBy.getName()));
    }
}
