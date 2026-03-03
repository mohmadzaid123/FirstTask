package org.example.library.exceptions;

public class InvalidLibraryItemException extends RuntimeException {
    public InvalidLibraryItemException() {
        super("item is null");
    }

    public InvalidLibraryItemException(Class<?> actualType) {
        super("item must be a LibraryItem, but was: " + actualType.getSimpleName());
    }
}
