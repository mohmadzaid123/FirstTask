package org.example.library.utils;

import org.example.library.domain.LibraryItem;

import java.util.function.Predicate;

public final class LibraryPredicates {
    private LibraryPredicates() {}

    // checks isAvailable()
    public static final Predicate<LibraryItem> IS_AVAILABLE =
            LibraryItem::isAvailable;

    // factory method returning a Predicate
    public static Predicate<LibraryItem> PUBLISHED_AFTER(int year) {
        return item -> item.getYearPublished() > year;
    }

    // case-insensitive keyword check
    public static Predicate<LibraryItem> TITLE_CONTAINS(String keyword) {
        String k = (keyword == null) ? "" : keyword.toLowerCase();
        return item -> item.getTitle() != null && item.getTitle().toLowerCase().contains(k);
    }
}