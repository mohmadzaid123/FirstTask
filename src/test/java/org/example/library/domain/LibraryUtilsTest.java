package org.example.library.domain;

import org.example.library.utils.LibraryUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryUtilsTest {

    @Test
    void printInfo_item_shouldPrintSummary() {
        Book book = new Book("B1", "Clean Code", 2008, "Robert C. Martin", "9780132350884");

        String out = captureOut(() -> LibraryUtils.printInfo(book));
        assertTrue(out.toLowerCase().contains("clean code"));
    }

    @Test
    void printInfo_itemVerboseFalse_shouldBehaveLikeSimple() {
        Book book = new Book("B1", "Clean Code", 2008, "Robert C. Martin", "9780132350884");

        String out = captureOut(() -> LibraryUtils.printInfo(book, false));
        assertTrue(out.toLowerCase().contains("clean code"));
        assertFalse(out.toLowerCase().contains("id:"));
    }

    @Test
    void printInfo_itemVerboseTrue_shouldPrintDetails() {
        Book book = new Book("B1", "Clean Code", 2008, "Robert C. Martin", "9780132350884");

        String out = captureOut(() -> LibraryUtils.printInfo(book, true));
        assertTrue(out.toLowerCase().contains("id:"));
        assertTrue(out.toLowerCase().contains("title:"));
        assertTrue(out.toLowerCase().contains("year:"));
        assertTrue(out.toLowerCase().contains("summary:"));
    }

    @Test
    void printInfo_list_shouldPrintAllItems() {
        Book b1 = new Book("B1", "Clean Code", 2008, "Robert C. Martin", "9780132350884");
        Book b2 = new Book("B2", "Refactoring", 1999, "Martin Fowler", "9780201485677");

        String out = captureOut(() -> LibraryUtils.printInfo(List.of(b1, b2)));
        assertTrue(out.toLowerCase().contains("clean code"));
        assertTrue(out.toLowerCase().contains("refactoring"));
    }

    private String captureOut(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString();
    }
}