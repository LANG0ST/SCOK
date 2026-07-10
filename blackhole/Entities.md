# New concepts Learned : 

# You can add constraints at DB level and name them.

    @Table(name = "room_memberships", uniqueConstraints = @UniqueConstraint(name = "joinSameRoomConstraint",
    columnNames = {"user_id","room_id"}))


# You can use Instant intead of LocalTimeDate


    @CreationTimestamp
    private Instant createdAt;

# You can promote the join table to a first-class entity. 

    Instead of using @ManyToMany you can model the join table as its own entity 
    dis gives you full control over the relationship and lets you store additional 
    data like date imta syfti