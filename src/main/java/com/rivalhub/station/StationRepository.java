package com.rivalhub.station;

import com.rivalhub.organization.Organization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends CrudRepository<Station, Long> {

//    public List<Station> findAllById(List<Long> );
}
