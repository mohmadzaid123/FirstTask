// src/test/java/org/example/library/concurrent/CheckoutSimulatorTest.java
package org.example.library.concurrent;

import org.example.library.contracts.Repository;
import org.example.library.domain.Book;
import org.example.library.domain.Member;
import org.example.library.repositories.InMemoryRepository; // <-- change if your repo package is different
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutSimulatorTest {

    @Test
    void atomicCountersAreCorrectAfterConcurrentSimulation() throws Exception {

        Repository<Book, String> repo = new InMemoryRepository<>();

        repo.save(new Book("B1", "A", 2000, "Auth1", "ISBN1"));
        repo.save(new Book("B2", "B", 2001, "Auth2", "ISBN2"));
        repo.save(new Book("B3", "C", 2002, "Auth3", "ISBN3"));

        List<Member> members = List.of(
                new Member("M1", "Ali", "ali@mail.com"),
                new Member("M2", "Sara", "sara@mail.com"),
                new Member("M3", "Omar", "omar@mail.com")
        );

        LibraryStats stats = new LibraryStats();
        CheckoutService service = new CheckoutService(repo, stats);
        CheckoutSimulator sim = new CheckoutSimulator(service, repo);

        CheckoutSimulator.SimulationResult result = sim.run(members, 20, 999L);

        // Atomics match the result
        assertEquals(result.checkouts(), stats.totalCheckouts.get());
        assertEquals(result.failures(), stats.totalFailures.get());
        assertEquals(result.totalMs(), stats.totalProcessingTimeMs.get());

        // every task -> either success or failure
        assertEquals(20, result.checkouts() + result.failures());

        // only 3 books exist -> max success is 3
        assertTrue(result.checkouts() <= 3);
    }
}