package com.rivalhub.event.darts.match.result;

import com.rivalhub.event.darts.match.result.Leg;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegRepository extends CrudRepository<Leg,Long> {

}
