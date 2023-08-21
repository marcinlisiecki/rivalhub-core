package com.rivalhub.station;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StationRepository extends CrudRepository<Station, Long> {
}
