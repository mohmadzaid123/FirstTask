package org.example.library.domain;

import org.example.library.contracts.Borrowable;
import org.example.library.contracts.Searchable;
import org.example.library.contracts.Identifiable;

import org.example.library.exceptions.InvalidMemberException;
import org.example.library.exceptions.ItemAlreadyBorrowedException;
import org.example.library.exceptions.ItemNotBorrowedException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a book item in the library.
 */
public class Book extends LibraryItem<String> implements Borrowable, Searchable,Identifiable<String>  {

    protected final String author;
    protected final String isbn;
    private boolean available = true;
    private Member borrowedBy;
    private int pageCount;
    private List<String> keywords;

    public Book(String id, String title, int yearPublished, String author, String isbn) {
        super(id, title, yearPublished);
        this.author = author;
        this.isbn = isbn;
    }

    @Override
    public String getId() {
        return id;
    }
    public int getPageCount() {
        return pageCount;
    }

    public List<String> getKeywords() {
        return keywords;
    }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public boolean isAvailable() { return available; }

    @Override
    public String getSummary() {
        return "Book: " + title + " by " + author + " (" + yearPublished + ")";
    }



    public void checkOut(Member member) {
        if (member == null) throw new InvalidMemberException();

        if (this.available) {
            this.available = false;
            this.borrowedBy = member;
            System.out.println("Book " + this.title + " borrowed by " + member.getName());
        } else {
            throw new ItemAlreadyBorrowedException(this, this.borrowedBy);
        }
    }

    @Override
    public void returnItem() {
        if (this.available) throw new ItemNotBorrowedException(this);

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