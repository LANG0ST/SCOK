package org.langost.scok.exceptions;

public class NotMemberException extends RuntimeException {
    public NotMemberException(Long userId, Long roomId) {
        super("User " + userId + " is not a member of room " + roomId);
    }
    }