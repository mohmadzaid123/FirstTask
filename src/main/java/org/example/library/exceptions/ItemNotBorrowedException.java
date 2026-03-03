package org.example.library.exceptions;

import org.example.library.domain.Book;

public class ItemNotBorrowedException extends RuntimeException {
    public ItemNotBorrowedException(Book book) {
        super("Book '" + book.getTitle() + "' is not borrowed.");
    }
}
