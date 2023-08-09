package com.rivalhub.event.pingpong;

import org.springframework.data.repository.CrudRepository;

public interface PingPongEventRepository extends CrudRepository<PingPongEvent,Long> {
}
