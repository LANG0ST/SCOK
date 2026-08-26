package org.langost.scok.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("user Not Found" + email);
    }
}
