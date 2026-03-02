package org.example.library.catalog;

import org.example.library.domain.Book;

import java.util.*;

public class LibraryCatalog {


    private final Set<String> isbns = new HashSet<>();


    private final Map<String, List<Book>> byAuthor = new HashMap<>();


    public boolean addBook(Book book) {
        if (!isbns.add(book.getIsbn())) return false;

        byAuthor.computeIfAbsent(book.getAuthor(), a -> new ArrayList<>()).add(book);
        return true;
    }


    public Set<String> getIsbns() {
        return Collections.unmodifiableSet(isbns);
    }

    public List<Book> getBooksByAuthor(String author) {
        return byAuthor.getOrDefault(author, List.of());
    }

    public Map<String, List<Book>> getGroupedByAuthor() {
        return Collections.unmodifiableMap(byAuthor);
    }
}
