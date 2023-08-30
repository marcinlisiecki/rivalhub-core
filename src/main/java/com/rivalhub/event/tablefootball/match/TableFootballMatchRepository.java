package com.rivalhub.event.tablefootball.match;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableFootballMatchRepository extends CrudRepository<TableFootballMatch, Long> {
}
