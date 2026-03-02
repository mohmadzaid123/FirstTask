package org.example.library.utils;

import org.example.library.domain.LibraryItem;

import java.util.List;

/**
 * Helper utilities (overloaded methods).
 */
public class LibraryUtils {

    public static void printInfo(LibraryItem item) {
        System.out.println(item.getSummary());
    }

    public static void printInfo(LibraryItem item, boolean verbose) {
        if (!verbose) {
            printInfo(item);
            return;
        }
        System.out.println("ID: " + item.getId());
        System.out.println("Title: " + item.getTitle());
        System.out.println("Year: " + item.getYearPublished());
        System.out.println("Summary: " + item.getSummary());
    }

    public static void printInfo(List<LibraryItem> items) {
        for (LibraryItem item : items) {
            printInfo(item);
        }
    }
}