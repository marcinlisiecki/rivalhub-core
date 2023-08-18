package com.rivalhub.event.tablefootball;

import com.rivalhub.event.pingpong.PingPongEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface TableFootballEventRepository extends CrudRepository<TableFootballEvent,Long> {

    @Query(value = "SELECT * FROM TABLE_FOOTBALL_EVENT \n" +
            "JOIN TABLE_FOOTBALL_EVENT_PARTICIPANTS ON PING_PONG_EVENT_EVENT_ID = EVENT_ID\n" +
            "WHERE ORGANIZATION_ORGANIZATION_ID = ?1\n" +
            "AND PARTICIPANTS_USER_ID = ?2", nativeQuery = true)
    Set<TableFootballEvent> findAllByOrganizationIdAndUserId(Long organizationId, Long UserId);
}
