package org.langost.scok.aspect;

import org.langost.scok.dto.response.ErrorPayload;
import org.langost.scok.exceptions.NotMemberException;
import org.langost.scok.exceptions.RoomInactiveException;
import org.langost.scok.exceptions.RoomNotFoundException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class StompExceptionHandler {

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorPayload handle(RoomNotFoundException ex) {
        return new ErrorPayload("ROOM_NOT_FOUND", ex.getMessage());
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorPayload handle(NotMemberException ex) {
        return new ErrorPayload("FORBIDDEN", ex.getMessage());

    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorPayload handle(RoomInactiveException ex) {
        return new ErrorPayload("ROOM_INACTIVE", ex.getMessage());

    }
}
