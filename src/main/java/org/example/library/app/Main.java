package org.example.library.app;

import org.example.library.domain.Book;
import org.example.library.domain.Member;
import org.example.library.utils.LibraryUtils;

public class Main {
    public static void main(String[] args) {
        Book b = new Book("B1", "Clean Code", 2008, "Robert C. Martin", "9780132350884");
        Member m = new Member("M1", "Mohammad", "m@example.com");

        m.borrow(b);
        LibraryUtils.printInfo(b, true);
        m.returnItem(b);
    }
}