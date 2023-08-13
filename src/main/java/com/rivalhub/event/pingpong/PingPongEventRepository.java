package com.rivalhub.event.pingpong;

import com.rivalhub.organization.Organization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface PingPongEventRepository extends CrudRepository<PingPongEvent,Long> {
    List<PingPongEvent> findAllByOrganization(Organization organization);

    @Query(value = "SELECT * FROM PING_PONG_EVENT \n" +
            "JOIN PING_PONG_EVENT_PARTICIPANTS ON PING_PONG_EVENT_EVENT_ID = EVENT_ID\n" +
            "WHERE ORGANIZATION_ORGANIZATION_ID = 1\n" +
            "AND PARTICIPANTS_USER_ID = 1", nativeQuery = true)
    Set<PingPongEvent> findAllByOrganizationIdAndUserId(Long organizationId, Long UserId);
}
