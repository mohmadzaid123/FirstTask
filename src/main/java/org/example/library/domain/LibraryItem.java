package org.example.library.domain;

import java.util.Objects;

/**
 * Base abstract type for all library items.
 */
public abstract class LibraryItem<TID> {

    protected  TID id;
    protected final String title;
    protected final  int yearPublished;

    public LibraryItem(TID id, String title, int yearPublished) {
        this.id = id;
        this.title = title;
        this.yearPublished = yearPublished;
    }

    public TID getId() { return id; }
    public  void setId(TID id){this.id = id;}
    public String getTitle() { return title; }
    public int getYearPublished() { return yearPublished; }

    public abstract String getSummary();

    public boolean isAvailable() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}