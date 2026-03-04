// org.example.library.concurrent/CheckoutSimulator.java
package org.example.library.concurrent;

import org.example.library.contracts.Repository;
import org.example.library.domain.Book;
import org.example.library.domain.Member;

import java.util.ArrayList;
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

    // Phase 5 requirement (members, threads)
    public SimulationResult run(int members, int threads) throws InterruptedException {
        return run(members, threads, 20, 42L, false);
    }

    // overload: stronger stress
    public SimulationResult run(int members, int threads, int tasks, long seed) throws InterruptedException {
        return run(members, threads, tasks, seed, false);
    }

    // ✅ overload: includeReturns = true => checkout + return
    public SimulationResult run(int members, int threads, int tasks, long seed, boolean includeReturns)
            throws InterruptedException {

        int safeMembers = Math.max(1, members);
        int safeThreads = Math.max(1, threads);
        int safeTasks = Math.max(1, tasks);

        List<Member> ms = new ArrayList<>();
        for (int i = 1; i <= safeMembers; i++) {
            ms.add(new Member("M-" + i, "Member-" + i, "m" + i + "@mail.com"));
        }

        return run(ms, safeTasks, seed, safeThreads, includeReturns);
    }

    // keep original signature
    public SimulationResult run(List<Member> members, int tasks, long seed) throws InterruptedException {
        return run(members, tasks, seed, 5, false);
    }

    private SimulationResult run(List<Member> members, int tasks, long seed, int threads, boolean includeReturns)
            throws InterruptedException {

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(tasks);

        Random rnd = new Random(seed);
        List<Book> books = bookRepo.findAll();
        if (books.isEmpty()) throw new IllegalStateException("No books in repository");

        for (int i = 0; i < tasks; i++) {
            pool.submit(() -> {
                try {
                    Member m = members.get(rnd.nextInt(members.size()));
                    Book b = books.get(rnd.nextInt(books.size()));

                    if (includeReturns) {
                        // 50% checkout / 50% return
                        if (rnd.nextBoolean()) {
                            checkoutService.checkOutBook(b.getId(), m);
                        } else {
                            checkoutService.returnBook(b.getId(), m);
                        }
                    } else {
                        checkoutService.checkOutBook(b.getId(), m);
                    }
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