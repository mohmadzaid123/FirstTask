// org.example.library/LibraryApp.java
package org.example.library;

import org.example.library.annotations.AnnotationProcessor;
import org.example.library.annotations.Version;
import org.example.library.concurrent.CheckoutService;
import org.example.library.concurrent.CheckoutSimulator;
import org.example.library.concurrent.LibraryStats;
import org.example.library.contracts.Repository;
import org.example.library.domain.Book;
import org.example.library.domain.Member;
import org.example.library.repositories.InMemoryRepository;

@Version(major = 1, minor = 0, author = "Mohammad")
public class LibraryApp {

    public static void main(String[] args) throws Exception {

        // 1) Print version info at startup
        AnnotationProcessor.printVersionInfo(LibraryApp.class);

        // 2) Create repo + audited proxy (logs @Audited methods)
        InMemoryRepository<Book, String> realRepo = new InMemoryRepository<>();
        Repository<Book, String> repo =
                AnnotationProcessor.createAuditedProxy(Repository.class, realRepo);

        // 3) Add sample books (IMPORTANT: few books => more collisions => stronger stress)
        repo.save(new Book("B1", "Clean Code", 2008, "Robert C. Martin", "9780132350884"));
        repo.save(new Book("B2", "Effective Java", 2018, "Joshua Bloch", "9780134685991"));
        repo.save(new Book("B3", "Java Concurrency in Practice", 2006, "Brian Goetz", "9780321349606"));

        // 4) Stats + services
        LibraryStats stats = new LibraryStats();
        CheckoutService checkoutService = new CheckoutService(repo, stats);
        CheckoutSimulator simulator = new CheckoutSimulator(checkoutService, repo);


        int members = 3;          // more members
        int threads = 10;          // parallel threads
        int tasks = threads * 2000; // heavy load
        long seed = 12345L;

        CheckoutSimulator.SimulationResult result =
                simulator.run(members, threads, tasks, seed, true);

        // 6) Print summary (UPDATED)
        System.out.println("==================================");
        System.out.println("[SUMMARY]");
        System.out.println("Tasks requested            : " + tasks);
        System.out.println("Total operations recorded  : " + stats.totalOps.get());
        System.out.println("Successful checkouts       : " + stats.totalCheckouts.get());
        System.out.println("Successful returns         : " + stats.totalReturns.get());
        System.out.println("Failures (all)             : " + stats.totalFailures.get());
        System.out.println("Return failures only       : " + stats.totalReturnFailures.get());
        System.out.println("Total time (ms)            : " + stats.totalProcessingTimeMs.get());
        System.out.println("==================================");
    }
}