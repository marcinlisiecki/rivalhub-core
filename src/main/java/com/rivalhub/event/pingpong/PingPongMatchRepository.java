package com.rivalhub.event.pingpong;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PingPongMatchRepository extends CrudRepository<PingPongMatch,Long> {

}
