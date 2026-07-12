package org.langost.scok.repository;

import org.langost.scok.entity.Room;
import org.langost.scok.entity.RoomMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomMembershipRepository extends JpaRepository<RoomMembership,Long> {

    @Query("""
            SELECT rm.room 
            FROM  RoomMembership rm
            WHERE rm.user.id=:userId           
            """)
    List<Room> findRoomsByUserId(@Param("userId")Long userId);
    boolean existsByRoomIdAndUserId(Long roomId, Long userId);
    Optional<RoomMembership> findByRoomIdAndUserId(Long roomId, Long userId);
    List<RoomMembership> findByRoomId(Long roomId);
    List<RoomMembership> findByRoomIdOrderByJoinedAtAsc(Long roomId);



}
