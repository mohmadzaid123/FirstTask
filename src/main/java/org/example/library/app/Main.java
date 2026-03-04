package org.example.library.app;

import org.example.library.annotations.AnnotationProcessor;
import org.example.library.catalog.LibraryCatalog;
import org.example.library.concurrent.CheckoutService;
import org.example.library.concurrent.CheckoutSimulator;
import org.example.library.concurrent.LibraryStats;
import org.example.library.domain.Book;
import org.example.library.domain.LibraryItem;
import org.example.library.domain.Member;
import org.example.library.repositories.BookRepository;
import org.example.library.repositories.MemberRepository;
import org.example.library.services.LibraryService;
import org.example.library.services.LibraryServiceApi;
import org.example.library.services.LibraryStreamService;
import org.example.library.utils.LibraryPredicates;

import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // 1) Build dependencies
        BookRepository bookRepo = new BookRepository();
        MemberRepository memberRepo = new MemberRepository();
        LibraryCatalog catalog = new LibraryCatalog();
        LibraryStreamService streamService = new LibraryStreamService();
        LibraryStats stats = new LibraryStats();
        // TODO: adjust constructor based on your real CheckoutService
        CheckoutService checkoutService = new CheckoutService(bookRepo, stats); // <-- عدّلها حسب كودك
        CheckoutSimulator simulator = new CheckoutSimulator(checkoutService, bookRepo);

        // 2) Create real service
        LibraryServiceApi real = new LibraryService(
                bookRepo,
                memberRepo,
                catalog,
                simulator,
                streamService
        );

        // 3) Wrap with proxy so @Audited + @ValidMember work
        LibraryServiceApi svc = AnnotationProcessor.createAuditedProxy(LibraryServiceApi.class, real);

        // optional: print version at startup
        AnnotationProcessor.printVersionInfo(LibraryService.class);

        // -----------------------------
        // Demo Phase 5 operations
        // -----------------------------

        // register member (ValidMember will be checked by proxy)
        Member m1 = new Member("M-1", "Ali", "ali@mail.com");
        System.out.println("registerMember -> " + svc.registerMember(m1));

        // add books (deduplicate via catalog ISBN)
        Book b1 = new Book("B-1", "Clean Code", 2008, "Robert Martin", "ISBN-111");
        Book b2 = new Book("B-2", "Clean Architecture", 2017, "Robert Martin", "ISBN-222");
        Book dupIsbn = new Book("B-3", "Duplicate", 2020, "X", "ISBN-111");

        System.out.println("addBook b1 -> " + svc.addBook(b1));
        System.out.println("addBook b2 -> " + svc.addBook(b2));
        System.out.println("addBook dupIsbn -> " + svc.addBook(dupIsbn)); // expected false

        // checkout / return (thread-safe)
        System.out.println("checkOutBook -> " + svc.checkOutBook("B-1", "M-1"));
        System.out.println("checkOutBook again -> " + svc.checkOutBook("B-1", "M-1")); // expected false
        System.out.println("returnBook -> " + svc.returnBook("B-1", "M-1"));

        // search books (stream-based + predicate filter)
        List<LibraryItem> found = svc.searchBooks("clean", LibraryPredicates.IS_AVAILABLE);
        System.out.println("searchBooks -> " + found.size());

        // top N recent available books
        System.out.println("top recent -> " + svc.getTopNRecentBooks(2));

        // member report
        System.out.println("member report:\n" + svc.getMemberReport("M-1"));

        // run concurrent simulation
        var result = svc.runConcurrentSimulation(20, 10);
        System.out.println("simulation -> " + result);
    }
}