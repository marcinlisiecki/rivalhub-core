package com.rivalhub.event.PingPong;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface PingPongEventRepository extends CrudRepository<PingPongEvent,Long> {
}
