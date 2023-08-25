package com.rivalhub.event.pullups.match.result;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PullUpSeriesRepository extends CrudRepository<PullUpSeries,Long> {
}
