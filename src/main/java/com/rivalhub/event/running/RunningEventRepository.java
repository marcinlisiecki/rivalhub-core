package com.rivalhub.event.running;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunningEventRepository extends CrudRepository<RunningEvent, Long> {
}
