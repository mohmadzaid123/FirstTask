package org.example.library.exceptions;

import org.example.library.domain.LibraryItem;
import org.example.library.domain.Member;

public class MemberDidNotBorrowItemException extends RuntimeException {
    public MemberDidNotBorrowItemException(Member member, LibraryItem item) {
        super("Member '" + member.getName() + "' did not borrow item '" + item.getTitle() + "'");
    }
}
