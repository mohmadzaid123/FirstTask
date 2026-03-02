package org.example.library.domain;

import org.example.library.contracts.Searchable;

/**
 * Represents a magazine item in the library.
 */
public class Magazine extends LibraryItem implements Searchable {

    private final int issueNumber;
    private final String publisher;

    public Magazine(String id, String title, int yearPublished, int issueNumber, String publisher) {
        super(id, title, yearPublished);
        this.issueNumber = issueNumber;
        this.publisher = publisher;
    }

    public int getIssueNumber() { return issueNumber; }
    public String getPublisher() { return publisher; }

    @Override
    public String getSummary() {
        return "Magazine: " + title + " (Issue " + issueNumber + ", " + publisher + ")";
    }

    @Override
    public boolean matchesQuery(String query) {
        if (query == null) return false;
        String q = query.trim().toLowerCase();
        return title.toLowerCase().contains(q)
                || publisher.toLowerCase().contains(q)
                || String.valueOf(issueNumber).contains(q);
    }
}