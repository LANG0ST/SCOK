# New concepts Learned :

# You can add IsActive to avoid problems when deleting a ROOM
      
        @Column(nullable = false)
        private Boolean isActive = true;

# You can ( Geniusment ) use UserPrincipal to authenticate some requests with the current User

        @AuthenticationPrincipal UserPrincipal principal

# You have to throw ResponseStatusException instead of general Exception so not everything is a 500 internal error frontendwise
    
    ResponseStatusException(HttpStatus.CONFLICT, "already a member")

# You can use the toResponse Pattern so reveal only what you want to the user

        return toResponse(membership);
        
        private RoomMembershipResponse toResponse(RoomMembership membership) {
        return new RoomMembershipResponse(
                membership.getUser().getId(),
                membership.getUser().getUsername(),
                membership.getJoinedAt()
        );

# You have to take into consideration ALL EDGE CASES
    
    What if the Owner deletes his own account?
    What if a Room is empty of users?
    What if The Owner Quits his Room?