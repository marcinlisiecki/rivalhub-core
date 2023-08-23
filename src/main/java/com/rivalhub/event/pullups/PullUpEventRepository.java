package com.rivalhub.event.pullups;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PullUpEventRepository extends CrudRepository<PullUpEvent,Long> {
    @Query(value = """
            SELECT * FROM PULL_UP_EVENT
            JOIN PULL_UP_EVENT_PARTICIPANTS ON PULL_UP_EVENT_EVENT_ID = EVENT_ID
            WHERE ORGANIZATION_ORGANIZATION_ID = ?1
            AND PARTICIPANTS_USER_ID = ?2
            """, nativeQuery = true)
    Set<PullUpEvent> findAllByOrganizationIdAndUserId(Long organizationId, Long UserId);
}
