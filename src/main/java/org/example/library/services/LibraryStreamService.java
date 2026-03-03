package org.example.library.services;

import org.example.library.domain.Book;
import org.example.library.domain.LibraryItem;
import org.example.library.domain.Member;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LibraryStreamService {

    // filter available, map to title
    public List<String> getTitlesAvailable(List<LibraryItem> items) {
        return items.stream()
                .filter(LibraryItem::isAvailable)
                .map(LibraryItem::getTitle)
                .collect(Collectors.toList());
    }

    // filter by type and year
    public List<Book> getBooksPublishedAfter(List<LibraryItem> items, int year) {
        return items.stream()
                .filter(i -> i instanceof Book)
                .map(i -> (Book) i)
                .filter(b -> b.getYearPublished() > year)
                .collect(Collectors.toList());
    }

    // distinct authors, sorted alphabetically
    public List<String> getDistinctAuthors(List<Book> books) {
        return books.stream()
                .map(Book::getAuthor)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    // flatMap each member's borrowed list
    public List<LibraryItem> getAllBorrowedItems(List<Member> members) {
        return members.stream()
                .flatMap(m -> m.getBorrowedItems().stream())
                .collect(Collectors.toList());
    }

    // each Book has keywords list; flatten them all
    public List<String> getAllKeywords(List<Book> books) {
        return books.stream()
                .flatMap(b -> b.getKeywords().stream())
                .collect(Collectors.toList());
    }

    // reduce to sum pages
    public int getTotalPages(List<Book> books) {
        return books.stream()
                .map(Book::getPageCount)
                .reduce(0, Integer::sum);
    }

    // reduce to find max year
    public Optional<Book> getMostRecentBook(List<Book> books) {
        return books.stream()
                .reduce((b1, b2) -> b1.getYearPublished() >= b2.getYearPublished() ? b1 : b2);
    }

    // min by year
    public Optional<LibraryItem> getOldestItem(List<LibraryItem> items) {
        return items.stream()
                .min(Comparator.comparingInt(LibraryItem::getYearPublished));
    }

    // max by year
    public Optional<LibraryItem> getNewestItem(List<LibraryItem> items) {
        return items.stream()
                .max(Comparator.comparingInt(LibraryItem::getYearPublished));
    }

    // groupingBy author
    public Map<String, List<Book>> groupBooksByAuthor(List<Book> books) {
        return books.stream()
                .collect(Collectors.groupingBy(Book::getAuthor));
    }

    // partitioningBy availability
    public Map<Boolean, List<LibraryItem>> partitionByAvailability(List<LibraryItem> items) {
        return items.stream()
                .collect(Collectors.partitioningBy(LibraryItem::isAvailable));
    }

    // count available books
    public long countAvailableBooks(List<LibraryItem> items) {
        return items.stream()
                .filter(i -> i instanceof Book)
                .filter(LibraryItem::isAvailable)
                .count();
    }

    // BONUS: more readable than reduce
    public Optional<Book> getMostRecentBookUsingMax(List<Book> books) {
        return books.stream().max(Comparator.comparingInt(Book::getYearPublished));
    }
}