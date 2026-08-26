package org.langost.scok.exceptions;

public class RoomInactiveException extends RuntimeException {
    public RoomInactiveException(Long roomId) { super("Room is not  active: " + roomId); }
}