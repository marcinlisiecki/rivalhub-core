package com.rivalhub.event.billiards.match;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BilliardsMatchRepository extends CrudRepository<BilliardsMatch, Long> {
}
