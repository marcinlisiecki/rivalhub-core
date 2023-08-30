package com.rivalhub.event.darts.match.result;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DartRoundRepository extends CrudRepository<DartRound, Long> {
}
