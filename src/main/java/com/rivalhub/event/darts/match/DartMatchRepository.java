package com.rivalhub.event.darts.match;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DartMatchRepository extends CrudRepository<DartMatch,Long> {
}
