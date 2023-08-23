package com.rivalhub.event.darts;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface DartEventRepository extends CrudRepository<DartEvent, Long> {
    @Query(value = """
            SELECT * FROM DART_EVENT
            JOIN DART_EVENT_PARTICIPANTS ON DART_EVENT_EVENT_ID = EVENT_ID
            WHERE ORGANIZATION_ORGANIZATION_ID = ?1
            AND PARTICIPANTS_USER_ID = ?2
            """, nativeQuery = true)
    Set<DartEvent> findAllByOrganizationIdAndUserId(Long organizationId, Long UserId);
}
