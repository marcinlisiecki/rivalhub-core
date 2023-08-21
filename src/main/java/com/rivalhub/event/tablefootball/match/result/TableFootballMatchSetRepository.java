package com.rivalhub.event.tablefootball.match.result;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableFootballMatchSetRepository extends CrudRepository<TableFootballMatchSet,Long> {
}
