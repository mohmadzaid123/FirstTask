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
import org.example.library.repositories.InMemoryRepository; // <-- if your path is different, change it

import java.util.List;

@Version(major = 1, minor = 0, author = "Mohammad")
public class LibraryApp {

    public static void main(String[] args) throws Exception {

        // 1) Print version info at startup
        AnnotationProcessor.printVersionInfo(LibraryApp.class);

        // 2) Create repo + audited proxy (logs @Audited methods)
        InMemoryRepository<Book, String> realRepo = new InMemoryRepository<>();
        Repository<Book, String> repo =
                AnnotationProcessor.createAuditedProxy(Repository.class, realRepo);

        // 3) Add sample books
        repo.save(new Book("B1", "Clean Code", 2008, "Robert C. Martin", "9780132350884"));
        repo.save(new Book("B2", "Effective Java", 2018, "Joshua Bloch", "9780134685991"));
        repo.save(new Book("B3", "Java Concurrency in Practice", 2006, "Brian Goetz", "9780321349606"));

        // 4) Members
        List<Member> members = List.of(
                new Member("M1", "Ali", "ali@mail.com"),
                new Member("M2", "Sara", "sara@mail.com"),
                new Member("M3", "Omar", "omar@mail.com")
        );

        // 5) Run simulation
        LibraryStats stats = new LibraryStats();
        CheckoutService checkoutService = new CheckoutService(repo, stats);
        CheckoutSimulator simulator = new CheckoutSimulator(checkoutService, repo);

        CheckoutSimulator.SimulationResult result = simulator.run(members, 20, 12345L);

        // 6) Print summary
        System.out.println("==================================");
        System.out.println("[SUMMARY]");
        System.out.println("Successful checkouts : " + result.checkouts());
        System.out.println("Failed checkouts     : " + result.failures());
        System.out.println("Total time (ms)      : " + result.totalMs());
        System.out.println("==================================");
    }
}