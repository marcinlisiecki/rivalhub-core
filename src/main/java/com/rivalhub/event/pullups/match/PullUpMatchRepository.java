package com.rivalhub.event.pullups.match;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PullUpMatchRepository extends CrudRepository<PullUpMatch,Long> {
}
