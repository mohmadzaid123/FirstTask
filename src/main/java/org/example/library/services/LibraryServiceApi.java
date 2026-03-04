package org.example.library.services;

import org.example.library.annotations.Audited;
import org.example.library.annotations.ValidMember;
import org.example.library.concurrent.CheckoutSimulator;
import org.example.library.domain.Book;
import org.example.library.domain.LibraryItem;
import org.example.library.domain.Member;

import java.util.List;
import java.util.function.Predicate;

public interface LibraryServiceApi {

    @Audited(action = "ADD_BOOK")
    boolean addBook(Book book);

    @Audited(action = "REGISTER_MEMBER")
    boolean registerMember(@ValidMember Member member);

    @Audited(action = "CHECKOUT_BOOK")
    boolean checkOutBook(String bookId, String memberId);

    @Audited(action = "RETURN_BOOK")
    boolean returnBook(String bookId, String memberId);

    @Audited(action = "SEARCH_BOOKS")
    List<LibraryItem> searchBooks(String query, Predicate<LibraryItem> filter);

    @Audited(action = "TOP_N_RECENT_BOOKS")
    List<Book> getTopNRecentBooks(int n);

    @Audited(action = "MEMBER_REPORT")
    String getMemberReport(String memberId);

    @Audited(action = "RUN_CONCURRENT_SIMULATION")
    CheckoutSimulator.SimulationResult runConcurrentSimulation(int members, int threads) throws InterruptedException;
}