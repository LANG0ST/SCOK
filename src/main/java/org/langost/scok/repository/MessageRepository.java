package org.langost.scok.repository;

import org.langost.scok.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {
    List<Message> findByRoomIdOrderBySentAtAsc(Long roomId);
}
