package org.langost.scok.repository;

import org.langost.scok.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository  extends JpaRepository<Room,Long> {

    boolean existsByNameAndOwnerId(String name, long ownerId);


    Optional<Room> findByIdAndOwnerId(Long roomId, Long ownerId);

}
