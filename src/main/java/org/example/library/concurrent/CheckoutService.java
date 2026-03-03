// org.example.library.concurrent/CheckoutService.java
package org.example.library.concurrent;

import org.example.library.annotations.ValidMember;
import org.example.library.contracts.Repository;
import org.example.library.domain.Book;
import org.example.library.domain.Member;
import org.example.library.exceptions.InvalidMemberException;
import org.example.library.exceptions.ItemAlreadyBorrowedException;


import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class CheckoutService {

    private final Repository<Book, String> bookRepo;
    private final ReentrantLock lock = new ReentrantLock();
    private final LibraryStats stats;

    public CheckoutService(Repository<Book, String> bookRepo, LibraryStats stats) {
        this.bookRepo = bookRepo;
        this.stats = stats;
    }

    public void checkOutBook(String bookId, @ValidMember Member member) {
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            Optional<Book> ob = bookRepo.findById(bookId);
            if (ob.isEmpty()) {
                stats.totalFailures.incrementAndGet();
                return;
            }

            Book b = ob.get();


            try {
                b.checkOut(member);
                stats.totalCheckouts.incrementAndGet();
            } catch (ItemAlreadyBorrowedException | InvalidMemberException e) {
                stats.totalFailures.incrementAndGet();
            }


        } finally {
            stats.totalProcessingTimeMs.addAndGet(System.currentTimeMillis() - start);
            lock.unlock();
        }
    }

    public LibraryStats stats() {
        return stats;
    }
}