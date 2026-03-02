# Phase 2 — Written Explanation

## Why use Optional instead of null?
- Optional makes it clear that a value may be missing, so you must handle the "not found" case.
- It helps avoid NullPointerException because you don’t return null.
- It provides clean methods like: orElse(), map(), ifPresent() instead of many null checks.

## When would you use a Set vs. a List?
- Use a Set when you want unique values (no duplicates), like unique ISBNs.
- Use a List when you need ordering, allow duplicates, or want to access elements by index.