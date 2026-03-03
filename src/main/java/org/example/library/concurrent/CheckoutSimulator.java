// org.example.library.concurrent/CheckoutSimulator.java
package org.example.library.concurrent;

import org.example.library.contracts.Repository;
import org.example.library.domain.Book;
import org.example.library.domain.Member;


import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class CheckoutSimulator {

    private final CheckoutService checkoutService;
    private final Repository<Book, String> bookRepo;

    public CheckoutSimulator(CheckoutService checkoutService, Repository<Book, String> bookRepo) {
        this.checkoutService = checkoutService;
        this.bookRepo = bookRepo;
    }

    public SimulationResult run(List<Member> members, int tasks, long seed) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(tasks);

        Random rnd = new Random(seed); // reproducible
        List<Book> books = bookRepo.findAll();
        if (books.isEmpty()) throw new IllegalStateException("No books in repository");

        for (int i = 0; i < tasks; i++) {
            pool.submit(() -> {
                try {
                    Member m = members.get(rnd.nextInt(members.size()));
                    Book b = books.get(rnd.nextInt(books.size()));
                    checkoutService.checkOutBook(b.getId(), m);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        return new SimulationResult(
                checkoutService.stats().totalCheckouts.get(),
                checkoutService.stats().totalFailures.get(),
                checkoutService.stats().totalProcessingTimeMs.get()
        );
    }

    public record SimulationResult(int checkouts, int failures, long totalMs) {}
}