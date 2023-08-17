package com.rivalhub.event.billiards;

import com.rivalhub.event.pingpong.PingPongEvent;
import com.rivalhub.organization.Organization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface BilliardsEventRepository extends CrudRepository<BilliardsEvent,Long> {

//    List<PingPongEvent> findAllByOrganization(Organization organization);

//    @Query(value = "SELECT * FROM BILLIARDS_EVENT \n" +
//            "JOIN BILLIARDS_EVENT_PARTICIPANTS ON BILLIARDS_EVENT_EVENT_ID = EVENT_ID\n" +
//            "WHERE ORGANIZATION_ORGANIZATION_ID = ?1\n" +
//            "AND PARTICIPANTS_USER_ID = ?2", nativeQuery = true)
//    Set<PingPongEvent> findAllByOrganizationIdAndUserId(Long organizationId, Long UserId);
}
