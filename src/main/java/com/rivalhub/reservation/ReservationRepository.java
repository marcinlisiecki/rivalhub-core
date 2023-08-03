package com.rivalhub.reservation;

import com.rivalhub.organization.Organization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long>, CrudRepository<Reservation, Long> {

}
