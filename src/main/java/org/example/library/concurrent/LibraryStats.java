// org.example.library.concurrent/LibraryStats.java
package org.example.library.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LibraryStats {
    public final AtomicInteger totalCheckouts = new AtomicInteger(0);
    public final AtomicInteger totalFailures = new AtomicInteger(0);
    public final AtomicLong totalProcessingTimeMs = new AtomicLong(0);
}