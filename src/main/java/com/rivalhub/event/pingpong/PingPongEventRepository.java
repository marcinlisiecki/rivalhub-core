package com.rivalhub.event.pingpong;

import com.rivalhub.organization.Organization;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PingPongEventRepository extends CrudRepository<PingPongEvent,Long> {
    List<PingPongEvent> findAllByOrganization(Organization organization);
}
