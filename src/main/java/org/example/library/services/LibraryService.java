package org.example.library.services;

import org.example.library.annotations.Version;
import org.example.library.catalog.LibraryCatalog;
import org.example.library.concurrent.CheckoutSimulator;
import org.example.library.domain.Book;
import org.example.library.domain.LibraryItem;
import org.example.library.domain.Member;
import org.example.library.repositories.BookRepository;
import org.example.library.repositories.MemberRepository;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Version(major = 1, minor = 0, author = "Mohammad")
public class LibraryService implements LibraryServiceApi {

    private final BookRepository bookRepo;
    private final MemberRepository memberRepo;
    private final LibraryCatalog catalog;
    private final CheckoutSimulator simulator;
    private final LibraryStreamService streamService;

    // protects checkout/return (shared state: availability + member borrowed list)
    private final ReentrantLock lock = new ReentrantLock(true);

    public LibraryService(BookRepository bookRepo,
                          MemberRepository memberRepo,
                          LibraryCatalog catalog,
                          CheckoutSimulator simulator,
                          LibraryStreamService streamService) {
        this.bookRepo = Objects.requireNonNull(bookRepo);
        this.memberRepo = Objects.requireNonNull(memberRepo);
        this.catalog = Objects.requireNonNull(catalog);
        this.simulator = Objects.requireNonNull(simulator);
        this.streamService = Objects.requireNonNull(streamService);
    }

    // 1) validate, deduplicate via catalog, save to repo
    @Override
    public boolean addBook(Book book) {
        if (book == null) return false;
        if (isBlank(book.getId()) || isBlank(book.getTitle()) || isBlank(book.getIsbn())) return false;

        boolean ok = catalog.addBook(book); // false if ISBN exists
        if (!ok) return false;

        bookRepo.save(book);
        return true;
    }

    // 2) validate with @ValidMember (proxy) + save
    @Override
    public boolean registerMember(Member member) {
        if (member == null) return false;
        if (isBlank(member.getMemberId()) || isBlank(member.getName()) || isBlank(member.getEmail())) return false;

        memberRepo.save(member);
        return true;
    }

    // 3) thread-safe checkout, returns boolean
    @Override
    public boolean checkOutBook(String bookId, String memberId) {
        if (isBlank(bookId) || isBlank(memberId)) return false;

        Optional<Book> bookOpt = bookRepo.findById(bookId);
        Optional<Member> memOpt = memberRepo.findById(memberId);
        if (bookOpt.isEmpty() || memOpt.isEmpty()) return false;

        Book book = bookOpt.get();
        Member member = memOpt.get();

        lock.lock();
        try {
            // Member.borrow(...) calls item.checkOut(this) already :contentReference[oaicite:6]{index=6}
            member.borrow(book);

            memberRepo.save(member);
            bookRepo.save(book);
            return true;
        } catch (RuntimeException ex) {
            // ItemAlreadyBorrowedException / InvalidLibraryItemException ... => false
            return false;
        } finally {
            lock.unlock();
        }
    }

    // 4) return, update availability, remove from member list
    @Override
    public boolean returnBook(String bookId, String memberId) {
        if (isBlank(bookId) || isBlank(memberId)) return false;

        Optional<Book> bookOpt = bookRepo.findById(bookId);
        Optional<Member> memOpt = memberRepo.findById(memberId);
        if (bookOpt.isEmpty() || memOpt.isEmpty()) return false;

        Book book = bookOpt.get();
        Member member = memOpt.get();

        lock.lock();
        try {
            // Member.returnItem(...) calls item.returnItem() and removes from list :contentReference[oaicite:7]{index=7}
            member.returnItem(book);
            memberRepo.save(member);
            bookRepo.save(book);
            return true;
        } catch (RuntimeException ex) {
            // MemberDidNotBorrowItemException / ItemNotBorrowedException ...
            return false;
        } finally {
            lock.unlock();
        }
    }

    // 5) stream-based search + extra filter
    @Override
    public List<LibraryItem> searchBooks(String query, Predicate<LibraryItem> filter) {
        String q = (query == null) ? "" : query.trim();
        Predicate<LibraryItem> f = (filter == null) ? it -> true : filter;

        return bookRepo.findAll().stream()
                .filter(Objects::nonNull)
                .filter(b -> b.matchesQuery(q))     // from Searchable
                .map(b -> (LibraryItem) b)
                .collect(Collectors.toList());
    }

    // 6) top N most recent AVAILABLE books
    @Override
    public List<Book> getTopNRecentBooks(int n) {
        if (n <= 0) return List.of();

        return bookRepo.findAll().stream()
                .filter(Objects::nonNull)
                .filter(Book::isAvailable)
                .sorted(Comparator.comparingInt(Book::getYearPublished).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    // 7) member report using streams
    @Override
    public String getMemberReport(String memberId) {
        if (isBlank(memberId)) return "Invalid memberId";

        Optional<Member> memOpt = memberRepo.findById(memberId);
        if (memOpt.isEmpty()) return "Member not found: " + memberId;

        Member m = memOpt.get();

        List<LibraryItem> items = m.getBorrowedItems(); // unmodifiable list :contentReference[oaicite:8]{index=8}

        long count = items.stream().count();

        String titles = items.stream()
                .map(LibraryItem::getTitle)
                .filter(Objects::nonNull)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining(", "));

        return "Member: " + m.getName() + " (" + m.getMemberId() + ")\n"
                + "Borrowed count: " + count + "\n"
                + "Titles: " + (titles.isBlank() ? "-" : titles);
    }

    // 8) stress test via simulator
    @Override
    public CheckoutSimulator.SimulationResult runConcurrentSimulation(int members, int threads) throws InterruptedException {

        return simulator.run(members, threads);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}