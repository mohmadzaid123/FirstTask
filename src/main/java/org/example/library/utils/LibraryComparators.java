package org.example.library.utils;

import org.example.library.domain.Book;
import org.example.library.domain.LibraryItem;

import java.util.Comparator;

public final class LibraryComparators {
    private LibraryComparators() {}

    // alphabetical by title
    public static final Comparator<LibraryItem> BY_TITLE =
            Comparator.comparing(LibraryItem::getTitle, String.CASE_INSENSITIVE_ORDER);

    // ascending by yearPublished
    public static final Comparator<LibraryItem> BY_YEAR =
            Comparator.comparingInt(LibraryItem::getYearPublished);

    // descending by yearPublished
    public static final Comparator<LibraryItem> BY_YEAR_DESC =
            Comparator.comparingInt(LibraryItem::getYearPublished).reversed();

    // for books only: sort by author, then title
    public static final Comparator<Book> BY_AUTHOR_THEN_TITLE =
            Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER);
}