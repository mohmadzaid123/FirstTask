package org.example.library.domain;

import org.example.library.contracts.Borrowable;
import org.example.library.contracts.Searchable;
import org.example.library.contracts.Identifiable;
/**
 * Represents a book item in the library.
 */
public class Book extends LibraryItem implements Borrowable, Searchable,Identifiable<String>  {

    protected final String author;
    protected final String isbn;
    private boolean available = true;
    private Member borrowedBy;

    public Book(String id, String title, int yearPublished, String author, String isbn) {
        super(id, title, yearPublished);
        this.author = author;
        this.isbn = isbn;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public boolean isAvailable() { return available; }

    @Override
    public String getSummary() {
        return "Book: " + title + " by " + author + " (" + yearPublished + ")";
    }

    @Override
    public void checkOut(Member member) {
        if (member == null) throw new IllegalArgumentException("member is null");

        if (this.available) {
            this.available = false;
            this.borrowedBy = member;
            System.out.println("Book " + this.title + " borrowed by " + member.getName());
        } else {
            String borrowerName = (this.borrowedBy == null) ? "unknown" : this.borrowedBy.getName();
            throw new IllegalStateException(
                    "Book '" + this.title + "' is already borrowed by " + borrowerName
            );
        }
    }

    @Override
    public void returnItem() {
        if (this.available) {
            throw new IllegalStateException("Book '" + this.title + "' is not borrowed.");
        }

        this.available = true;
        this.borrowedBy = null;
    }

    @Override
    public boolean matchesQuery(String query) {
        if (query == null) return false;
        String q = query.trim().toLowerCase();
        return title.toLowerCase().contains(q)
                || author.toLowerCase().contains(q)
                || isbn.toLowerCase().contains(q);
    }



}