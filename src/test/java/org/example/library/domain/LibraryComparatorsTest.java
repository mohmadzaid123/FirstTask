package org.example.library.domain;

import org.example.library.utils.LibraryComparators;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryComparatorsTest {

    @Test
    void sortByTitle() {
        List<LibraryItem> items = new ArrayList<>();
        items.add(new Book("1", "Zoo", 2010, "A", "ISBN1"));
        items.add(new Book("2", "Apple", 2012, "B", "ISBN2"));

        items.sort(LibraryComparators.BY_TITLE);

        assertEquals("Apple", items.get(0).getTitle());
        assertEquals("Zoo", items.get(1).getTitle());
    }

//    @Test
//    void sortByYearDesc() {
//        List<LibraryItem> items = new ArrayList<>();
//        items.add(new Book("1", "A", 2020, "X", "ISBN1"));
//        items.add(new Book("2", "B", 2010, "Y", "ISBN2"));
//
//        items.sort(LibraryComparators.BY_YEAR_DESC);
//
//        assertEquals(2020, items.get(0).getYearPublished());
//        assertEquals(2010, items.get(1).getYearPublished());
//    }

    @Test
    void sortByAuthorThenTitle_booksOnly() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("1", "Beta", 2020, "Ali", "I1"));
        books.add(new Book("2", "Alpha", 2021, "Ali", "I2"));
        books.add(new Book("3", "Anything", 2022, "Sara", "I3"));

        books.sort(LibraryComparators.BY_AUTHOR_THEN_TITLE);

        assertEquals("Ali", books.get(0).getAuthor());
        assertEquals("Alpha", books.get(0).getTitle());
    }
}