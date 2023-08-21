package com.rivalhub.event.pingpong;

import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface PingPongEventRepository extends CrudRepository<PingPongEvent,Long> {

}
