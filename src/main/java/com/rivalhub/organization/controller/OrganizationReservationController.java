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

    @PostMapping("reservations")
    private ResponseEntity<?> addReservations(@RequestBody AddReservationDTO reservationDTO){
        ReservationDTO reservation = organizationReservationService.addReservation(reservationDTO);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("{id}/reservations")
    private ResponseEntity<?> viewReservations(@PathVariable Long id){
        return ResponseEntity.ok(organizationReservationService.viewReservations(id));
    }

}
