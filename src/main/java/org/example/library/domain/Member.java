package org.example.library.domain;

import org.example.library.contracts.Borrowable;
import org.example.library.contracts.Identifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a library member (aggregation: borrowed items are not owned).
 */
public class Member implements Identifiable<String>{

    private final String memberId;
    private final String name;
    private final String email;

    private final List<LibraryItem> borrowedItems = new ArrayList<>();

    public Member(String memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
    }
    @Override
    public String getId() {
        return memberId;
    }
    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void borrow(Borrowable item) {
        if (item == null) throw new IllegalArgumentException("item is null");
        if (!(item instanceof LibraryItem li)) {
            throw new IllegalArgumentException("item must be a LibraryItem");
        }

        item.checkOut(this);
        if (!borrowedItems.contains(li)) {
            borrowedItems.add(li);
        }
    }

    public void returnItem(Borrowable item) {
        if (item == null) throw new IllegalArgumentException("item is null");

        if (!(item instanceof LibraryItem li)) {
            throw new IllegalArgumentException("item must be a LibraryItem");
        }

        if (!borrowedItems.contains(li)) {
            throw new IllegalStateException("This member did not borrow this item");
        }

        item.returnItem();
        borrowedItems.remove(li);
    }

    public List<LibraryItem> getBorrowedItems() {
        return Collections.unmodifiableList(borrowedItems);
    }
}