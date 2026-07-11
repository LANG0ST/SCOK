package org.langost.scok.repository;

import org.langost.scok.entity.RoomMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomMembershipRepository extends JpaRepository<RoomMembership,Long> {
}
