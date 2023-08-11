package com.rivalhub.organization.controller;

import com.rivalhub.organization.service.OrganizationReservationService;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.ReservationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationReservationController {

    private final OrganizationReservationService organizationReservationService;

    @PostMapping("{id}/reservations")
    private ResponseEntity<?> addReservations(@RequestBody AddReservationDTO reservationDTO,
                                      @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id){
        ReservationDTO reservation = organizationReservationService.addReservation(reservationDTO, id, userDetails.getUsername());
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("{id}/reservations")
    private ResponseEntity<?> viewReservations(@PathVariable Long id,
                                              @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(organizationReservationService.viewReservations(id, userDetails.getUsername()));
    }

}
