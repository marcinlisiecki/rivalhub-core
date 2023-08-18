package com.rivalhub.event.pullups;

import com.rivalhub.event.pingpong.PingPongEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PullUpEventRepository extends CrudRepository<PullUpEvent,Long> {
    @Query(value = "SELECT * FROM PULL_UP_EVENT \n" +
            "JOIN PULL_UP_EVENT_PARTICIPANTS ON PULL_UP_EVENT_EVENT_ID = EVENT_ID\n" +
            "WHERE ORGANIZATION_ORGANIZATION_ID = ?1\n" +
            "AND PARTICIPANTS_USER_ID = ?2", nativeQuery = true)
    Set<PullUpEvent> findAllByOrganizationIdAndUserId(Long organizationId, Long UserId);
}
