package org.example.library.contracts;

import org.example.library.domain.Member;

public interface Borrowable {
    void checkOut(Member member);
    void returnItem();
}