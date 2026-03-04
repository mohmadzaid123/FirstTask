package org.example.library.domain;

import org.example.library.exceptions.ItemAlreadyBorrowedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void getSummary_shouldContainImportantFields() {
        Book book = new Book("B1", "Clean Code", 2008, "Robert C. Martin", "9780132350884");
        String summary = book.getSummary();

        assertNotNull(summary);
        assertTrue(summary.toLowerCase().contains("clean code"));
        assertTrue(summary.toLowerCase().contains("robert"));

    }

    @Test
    void checkOut_shouldMakeBookUnavailable() {
        Book book = new Book("B1", "Clean Code", 2008, "Robert C. Martin", "9780132350884");
        Member member = new Member("M1", "Mohammad", "m@example.com");

        assertTrue(book.isAvailable());
        book.checkOut(member);
        assertFalse(book.isAvailable());
    }

    @Test
    void checkOut_whenAlreadyBorrowed_shouldThrow() {
        Book book = new Book("B1", "Clean Code", 2008, "Robert C. Martin", "9780132350884");
        Member m1 = new Member("M1", "A", "a@example.com");
        Member m2 = new Member("M2", "B", "b@example.com");

        book.checkOut(m1);
        assertThrows(ItemAlreadyBorrowedException.class, () -> book.checkOut(m2));
    }

    @Test
    void returnItem_shouldMakeBookAvailableAgain() {
        Book book = new Book("B1", "Clean Code", 2008, "Robert C. Martin", "9780132350884");
        Member member = new Member("M1", "Mohammad", "m@example.com");

        book.checkOut(member);
        assertFalse(book.isAvailable());

        book.returnItem();
        assertTrue(book.isAvailable());
    }
}