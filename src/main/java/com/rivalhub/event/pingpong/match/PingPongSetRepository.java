package com.rivalhub.event.pingpong.match;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PingPongSetRepository extends CrudRepository<PingPongSet,Long> {
}
