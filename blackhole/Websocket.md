# New concepts learned

### WebSocket and STOMP are not the same thing

STOMP is the messaging protocol used on top of that WebSocket connection. It adds
`CONNECT`, `SUBSCRIBE`, and `SEND`



### `/ws`

initial WebSocket connection endpoint:
ws://localhost:8080/ws

### `/topic`

`enableSimpleBroker("/topic")` enables Spring message broker

## `@MessageMapping` and `@SendTo`

```java
@MessageMapping("/rooms/{roomId}/messages")
@SendTo("/topic/rooms/{roomId}")
public MessageResponse sendMessage(...) {
    ...
}
```

`@MessageMapping` routes matching STOMP `SEND` frames to `sendMessage`.

`@SendTo` sends the method's return value to the specified broker destination.
Spring converts the returned `MessageResponse` to JSON.


## Complete message flow

```
1. Frontend connects to /ws and completes the WebSocket/STOMP handshake.
2. Frontend subscribes to /topic/rooms/12.
3. User presses Send.
4. Frontend sends a STOMP frame to /app/rooms/12/messages.
5. Spring routes it to sendMessage because of @MessageMapping.
6. MessageService checks the room and membership, then saves the message.
7. sendMessage returns MessageResponse.
8. @SendTo publishes that response to /topic/rooms/12.
9. The broker delivers it to every subscriber, including the sender.
```

## JWT @nd STOMP

Since we cannot authenticate the initial handshake we authenticate the next first action 
the CONNECT command. once the STOMP SESSION has a principal the other frames can just reuse it. 

## Custom Exceptions @nd Message Advice 

```java


    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorPayload handle(NotMemberException ex) {
        return new ErrorPayload("FORBIDDEN", ex.getMessage());

    }

```

## Hibernate: TransientPropertyValueException

Happens when saving an entity that references another entity Hibernate has never persisted 

Example bug:
```java
Room room = new Room();
room.setName(request.roomName());
room.setOwner(owner);

RoomMembership membership = new RoomMembership();
membership.setRoom(room);             
roomMembershipRepository.save(membership); 
```

Fix: persist the parent entity first, so it has a real ID
```java
room = roomRepository.save(room);      // room now has an ID
membership.setRoom(room);              // now points to a managed entity
roomMembershipRepository.save(membership);
```
