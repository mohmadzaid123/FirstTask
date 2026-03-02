package org.example.library.domain;

import org.example.library.utils.LibraryPredicates;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class LibraryPredicatesTest {

    @Test
    void publishedAfter_works() {
        LibraryItem item = new Book("1", "T", 2018, "A", "I1");
        assertTrue(LibraryPredicates.PUBLISHED_AFTER(2015).test(item));
        assertFalse(LibraryPredicates.PUBLISHED_AFTER(2020).test(item));
    }

    @Test
    void titleContains_caseInsensitive() {
        LibraryItem item = new Book("1", "Clean Code", 2008, "A", "I1");
        assertTrue(LibraryPredicates.TITLE_CONTAINS("cLeAn").test(item));
        assertFalse(LibraryPredicates.TITLE_CONTAINS("python").test(item));
    }

    @Test
    void and_negate_combination() {
        LibraryItem item = new Book("1", "Modern Java", 2019, "A", "I1");

        Predicate<LibraryItem> recentAndAvailable =
                LibraryPredicates.IS_AVAILABLE.and(LibraryPredicates.PUBLISHED_AFTER(2015));

        assertTrue(recentAndAvailable.test(item));

        Predicate<LibraryItem> notRecent =
                LibraryPredicates.PUBLISHED_AFTER(2015).negate();

        assertFalse(notRecent.test(item));
    }
}