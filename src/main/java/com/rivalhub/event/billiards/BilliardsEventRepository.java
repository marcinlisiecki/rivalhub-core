package com.rivalhub.event.billiards;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface BilliardsEventRepository extends CrudRepository<BilliardsEvent,Long> {

    @Query(value = "SELECT * FROM BILLIARDS_EVENT \n" +
            "JOIN BILLIARDS_EVENT_PARTICIPANTS ON BILLIARDS_EVENT_EVENT_ID = EVENT_ID\n" +
            "WHERE ORGANIZATION_ORGANIZATION_ID = ?1\n" +
            "AND PARTICIPANTS_USER_ID = ?2", nativeQuery = true)
    Set<BilliardsEvent> findAllByOrganizationIdAndUserId(Long organizationId, Long UserId);
}
