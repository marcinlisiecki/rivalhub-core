package com.rivalhub.organization;

import com.rivalhub.reservation.Reservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrganizationRepository extends PagingAndSortingRepository<Organization, Long>, CrudRepository<Organization, Long> {

}