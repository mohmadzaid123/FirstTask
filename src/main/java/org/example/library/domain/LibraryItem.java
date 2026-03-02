package org.example.library.domain;

/**
 * Base abstract type for all library items.
 */
public abstract class LibraryItem {

    protected final String id;
    protected final String title;
    protected final int yearPublished;

    public LibraryItem(String id, String title, int yearPublished) {
        this.id = id;
        this.title = title;
        this.yearPublished = yearPublished;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getYearPublished() { return yearPublished; }

    public abstract String getSummary();

    public boolean isAvailable() {
        return true; // default behavior (you can refine later)
    }
}