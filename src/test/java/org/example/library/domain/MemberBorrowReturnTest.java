package org.example.library.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberBorrowReturnTest {

    @Test
    void borrow_thenReturn_flowShouldUpdateMemberListAndBookAvailability() {
        Member member = new Member("M1", "Mohammad", "m@example.com");
        Book book = new Book("B1", "Clean Code", 2008, "Robert C. Martin", "9780132350884");

        // borrow
        assertTrue(book.isAvailable());
        member.borrow(book);
        assertFalse(book.isAvailable());
        assertEquals(1, member.getBorrowedItems().size());
        assertTrue(member.getBorrowedItems().contains(book));

        // return
        member.returnItem(book);
        assertTrue(book.isAvailable());
        assertEquals(0, member.getBorrowedItems().size());
        assertFalse(member.getBorrowedItems().contains(book));
    }

    @Test
    void returnItem_whenMemberDidNotBorrow_shouldThrow() {
        Member member = new Member("M1", "Mohammad", "m@example.com");
        Book book = new Book("B1", "Clean Code", 2008, "Robert C. Martin", "9780132350884");

        assertThrows(IllegalStateException.class, () -> member.returnItem(book));
    }
}