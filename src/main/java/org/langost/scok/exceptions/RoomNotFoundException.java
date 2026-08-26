package org.langost.scok.exceptions;

public class RoomNotFoundException  extends RuntimeException{
    public RoomNotFoundException(Long roomId){super("Room Not Found" + roomId);}
}
