package com.rivalhub.event.darts;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface DartEventRepository extends CrudRepository<DartEvent,Long> {

}
