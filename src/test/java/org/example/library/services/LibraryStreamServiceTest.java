package org.example.library.services;

import org.example.library.domain.Book;
import org.example.library.domain.LibraryItem;
import org.example.library.domain.Member;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LibraryStreamServiceTest {

    private final LibraryStreamService service = new LibraryStreamService();

    // ---------------------------
    // Helpers
    // ---------------------------

    private static Member member(String id) {
        return new Member(id, "Name-" + id, id + "@mail.com");
    }

    private static Book book(String id, String title, int year, String author, String isbn) {
        return new Book(id, title, year, author, isbn);
    }

    private static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            fail("Failed to set field '" + fieldName + "': " + e.getMessage());
        }
    }

    private static Book bookFull(String id, String title, int year, String author, String isbn,
                                 int pages, List<String> keywords, boolean available, Member borrower) {

        Book b = book(id, title, year, author, isbn);

        // Book has private fields: pageCount, keywords (no setters)
        setPrivateField(b, "pageCount", pages);
        setPrivateField(b, "keywords", keywords);

        // set availability using real behavior (checkout)
        if (!available) {
            if (borrower == null) borrower = member("borrower");
            b.checkOut(borrower);
        }

        return b;
    }

    // Dummy non-book item (for oldest/newest/availability tests)
    private static class DummyItem extends LibraryItem<String> {
        private final boolean available;

        public DummyItem(String id, String title, int year, boolean available) {
            super(id, title, year);
            this.available = available;
        }

        @Override
        public String getSummary() {
            return "Dummy: " + getTitle();
        }

        @Override
        public boolean isAvailable() {
            return available;
        }
    }

    // ---------------------------
    // Filtering & Mapping
    // ---------------------------

    @Test
    void getTitlesAvailable_emptyList() {
        assertEquals(List.of(), service.getTitlesAvailable(List.of()));
    }

    @Test
    void getTitlesAvailable_filtersAvailableAndMapsTitle() {
        Member m = member("m1");

        Book b1 = bookFull("1", "A", 2020, "Ali", "ISBN1", 100, List.of("k1"), true, null);
        Book b2 = bookFull("2", "B", 2021, "Omar", "ISBN2", 120, List.of("k2"), false, m); // unavailable
        DummyItem d1 = new DummyItem("d1", "D", 2019, true);

        List<String> titles = service.getTitlesAvailable(List.of(b1, b2, d1));
        assertEquals(List.of("A", "D"), titles);
    }

    @Test
    void getBooksPublishedAfter_emptyList() {
        assertEquals(List.of(), service.getBooksPublishedAfter(List.of(), 2015));
    }

    @Test
    void getBooksPublishedAfter_onlyBooksAndAfterYear() {
        Book oldBook = bookFull("1", "Old", 2010, "Ali", "ISBN1", 10, List.of(), true, null);
        Book newBook = bookFull("2", "New", 2022, "Ali", "ISBN2", 10, List.of(), true, null);
        DummyItem dummy = new DummyItem("d1", "X", 2023, true);

        List<Book> result = service.getBooksPublishedAfter(List.of(oldBook, newBook, dummy), 2015);
        assertEquals(1, result.size());
        assertEquals("New", result.get(0).getTitle());
    }

    @Test
    void getDistinctAuthors_emptyList() {
        assertEquals(List.of(), service.getDistinctAuthors(List.of()));
    }

    @Test
    void getDistinctAuthors_distinctAndSortedCaseInsensitive() {
        Book b1 = bookFull("1", "T1", 2020, "zaid", "I1", 1, List.of(), true, null);
        Book b2 = bookFull("2", "T2", 2021, "Ali", "I2", 1, List.of(), true, null);
        Book b3 = bookFull("3", "T3", 2022, "zaid", "I3", 1, List.of(), true, null);
        Book b4 = bookFull("4", "T4", 2023, "omar", "I4", 1, List.of(), true, null);

        List<String> authors = service.getDistinctAuthors(List.of(b1, b2, b3, b4));
        assertEquals(List.of("Ali", "omar", "zaid"), authors);
    }

    // ---------------------------
    // flatMap
    // ---------------------------

    @Test
    void getAllBorrowedItems_emptyMembers() {
        assertEquals(List.of(), service.getAllBorrowedItems(List.of()));
    }

    @Test
    void getAllBorrowedItems_flattensBorrowedLists() {
        Member m1 = member("m1");
        Member m2 = member("m2");

        Book b1 = bookFull("1", "A", 2020, "Ali", "ISBN1", 10, List.of(), true, null);
        Book b2 = bookFull("2", "B", 2021, "Omar", "ISBN2", 10, List.of(), true, null);
        Book b3 = bookFull("3", "C", 2022, "Sara", "ISBN3", 10, List.of(), true, null);

        m1.borrow(b1);
        m1.borrow(b2);
        m2.borrow(b3);

        List<LibraryItem> borrowed = service.getAllBorrowedItems(List.of(m1, m2));

        assertEquals(3, borrowed.size());
        assertTrue(borrowed.contains(b1));
        assertTrue(borrowed.contains(b2));
        assertTrue(borrowed.contains(b3));
    }

    @Test
    void getAllKeywords_emptyBooks() {
        assertEquals(List.of(), service.getAllKeywords(List.of()));
    }

    @Test
    void getAllKeywords_flattensKeywords() {
        Book b1 = bookFull("1", "A", 2020, "Ali", "ISBN1", 10, List.of("java", "streams"), true, null);
        Book b2 = bookFull("2", "B", 2021, "Omar", "ISBN2", 10, List.of("oop"), true, null);

        List<String> kws = service.getAllKeywords(List.of(b1, b2));
        assertEquals(List.of("java", "streams", "oop"), kws);
    }

    // ---------------------------
    // Reduce
    // ---------------------------

    @Test
    void getTotalPages_emptyIsZero() {
        assertEquals(0, service.getTotalPages(List.of()));
    }

    @Test
    void getTotalPages_sumsPages() {
        Book b1 = bookFull("1", "A", 2020, "Ali", "ISBN1", 100, List.of(), true, null);
        Book b2 = bookFull("2", "B", 2021, "Omar", "ISBN2", 50, List.of(), true, null);

        assertEquals(150, service.getTotalPages(List.of(b1, b2)));
    }

    @Test
    void getMostRecentBook_emptyOptional() {
        assertTrue(service.getMostRecentBook(List.of()).isEmpty());
    }

    @Test
    void getMostRecentBook_returnsMaxYear() {
        Book oldB = bookFull("1", "Old", 2000, "Ali", "ISBN1", 10, List.of(), true, null);
        Book newB = bookFull("2", "New", 2022, "Omar", "ISBN2", 10, List.of(), true, null);
        Book midB = bookFull("3", "Mid", 2010, "Sara", "ISBN3", 10, List.of(), true, null);

        Optional<Book> mostRecent = service.getMostRecentBook(List.of(oldB, newB, midB));
        assertTrue(mostRecent.isPresent());
        assertEquals("New", mostRecent.get().getTitle());
    }

    @Test
    void getMostRecentBookUsingMax_sameAsReduce() {
        Book b1 = bookFull("1", "Old", 2000, "Ali", "ISBN1", 10, List.of(), true, null);
        Book b2 = bookFull("2", "New", 2022, "Omar", "ISBN2", 10, List.of(), true, null);

        assertEquals(
                service.getMostRecentBook(List.of(b1, b2)).map(Book::getTitle),
                service.getMostRecentBookUsingMax(List.of(b1, b2)).map(Book::getTitle)
        );
    }

    // ---------------------------
    // min / max / collect
    // ---------------------------

    @Test
    void getOldestItem_emptyOptional() {
        assertTrue(service.getOldestItem(List.of()).isEmpty());
    }

    @Test
    void getNewestItem_emptyOptional() {
        assertTrue(service.getNewestItem(List.of()).isEmpty());
    }

    @Test
    void getOldestItem_minYear() {
        LibraryItem a = new DummyItem("d1", "A", 1990, true);
        LibraryItem b = new DummyItem("d2", "B", 2000, true);

        assertEquals("A", service.getOldestItem(List.of(a, b)).get().getTitle());
    }

    @Test
    void getNewestItem_maxYear() {
        LibraryItem a = new DummyItem("d1", "A", 1990, true);
        LibraryItem b = new DummyItem("d2", "B", 2000, true);

        assertEquals("B", service.getNewestItem(List.of(a, b)).get().getTitle());
    }

    @Test
    void groupBooksByAuthor_emptyMap() {
        assertTrue(service.groupBooksByAuthor(List.of()).isEmpty());
    }

    @Test
    void groupBooksByAuthor_groupsCorrectly() {
        Book b1 = bookFull("1", "A", 2020, "Ali", "ISBN1", 10, List.of(), true, null);
        Book b2 = bookFull("2", "B", 2021, "Ali", "ISBN2", 10, List.of(), true, null);
        Book b3 = bookFull("3", "C", 2022, "Omar", "ISBN3", 10, List.of(), true, null);

        Map<String, List<Book>> grouped = service.groupBooksByAuthor(List.of(b1, b2, b3));

        assertEquals(2, grouped.get("Ali").size());
        assertEquals(1, grouped.get("Omar").size());
    }

    @Test
    void partitionByAvailability_emptyHasBothKeys() {
        Map<Boolean, List<LibraryItem>> parts = service.partitionByAvailability(List.of());
        assertNotNull(parts.get(true));
        assertNotNull(parts.get(false));
        assertEquals(List.of(), parts.get(true));
        assertEquals(List.of(), parts.get(false));
    }

    @Test
    void partitionByAvailability_splitsTrueFalse() {
        Member m = member("m1");

        Book availableBook = bookFull("1", "A", 2020, "Ali", "ISBN1", 10, List.of(), true, null);
        Book unavailableBook = bookFull("2", "B", 2021, "Omar", "ISBN2", 10, List.of(), false, m);
        DummyItem availableDummy = new DummyItem("d1", "D", 2019, true);
        DummyItem unavailableDummy = new DummyItem("d2", "E", 2018, false);

        Map<Boolean, List<LibraryItem>> parts =
                service.partitionByAvailability(List.of(availableBook, unavailableBook, availableDummy, unavailableDummy));

        assertTrue(parts.get(true).contains(availableBook));
        assertTrue(parts.get(true).contains(availableDummy));
        assertTrue(parts.get(false).contains(unavailableBook));
        assertTrue(parts.get(false).contains(unavailableDummy));
    }

    @Test
    void countAvailableBooks_emptyIsZero() {
        assertEquals(0, service.countAvailableBooks(List.of()));
    }

    @Test
    void countAvailableBooks_countsOnlyAvailableBooks() {
        Member m = member("m1");

        Book availableBook = bookFull("1", "A", 2020, "Ali", "ISBN1", 10, List.of(), true, null);
        Book unavailableBook = bookFull("2", "B", 2021, "Omar", "ISBN2", 10, List.of(), false, m);
        DummyItem availableDummy = new DummyItem("d1", "D", 2019, true);

        assertEquals(1, service.countAvailableBooks(List.of(availableBook, unavailableBook, availableDummy)));
    }
}