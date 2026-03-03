package org.example.library.exceptions;

public class InvalidMemberException extends RuntimeException {
    public InvalidMemberException() {
        super("member is null");
    }
}
