package com.rivalhub.reservation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long>, CrudRepository<Reservation, Long> {


}
