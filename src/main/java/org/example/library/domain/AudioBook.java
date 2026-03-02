package org.example.library.domain;

/**
 * Represents an audiobook (a specialized Book).
 */
public class AudioBook extends Book {

    private final String narrator;
    private final int durationMinutes;

    public AudioBook(
            String id,
            String title,
            int yearPublished,
            String author,
            String isbn,
            String narrator,
            int durationMinutes
    ) {
        super(id, title, yearPublished, author, isbn);
        this.narrator = narrator;
        this.durationMinutes = durationMinutes;
    }

    public String getNarrator() { return narrator; }
    public int getDurationMinutes() { return durationMinutes; }

    @Override
    public String getSummary() {
        return "AudioBook: " + title + " by " + author + ", narrated by " + narrator +
                " (" + durationMinutes + " min)";
    }
}