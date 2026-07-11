package org.langost.scok.repository;

import org.langost.scok.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository  extends JpaRepository<Room,Long> {
}
