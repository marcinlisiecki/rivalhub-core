package com.rivalhub.organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.common.MergePatcher;
import com.rivalhub.email.EmailService;
import com.rivalhub.event.EventType;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.reservation.ReservationDTO;
import com.rivalhub.station.NewStationDto;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/organizations")
@AllArgsConstructor
public class OrganizationController {
    private OrganizationService organizationService;
    @GetMapping("{id}")
    public ResponseEntity<OrganizationDTO> viewOrganization(@PathVariable Long id){
        return ResponseEntity.ok(organizationService.findOrganization(id));
    }

    @PostMapping
    public ResponseEntity<OrganizationDTO> addOrganization(@RequestBody OrganizationCreateDTO organizationCreateDTO,
                                                           @AuthenticationPrincipal UserDetails userDetails){
        OrganizationDTO savedOrganization = organizationService.saveOrganization(organizationCreateDTO, userDetails.getUsername());
        URI savedOrganizationUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedOrganization.getId())
                .toUri();
        return ResponseEntity.created(savedOrganizationUri).body(savedOrganization);
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateOrganization(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        organizationService.updateOrganization(id, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/invitation")
    public ResponseEntity<?> createInvitation(@PathVariable Long id){
        return ResponseEntity.ok(organizationService.createInvitationHash(id));
    }

    @GetMapping("/{id}/invitation/{hash}")
    public ResponseEntity<?> addUser(@PathVariable Long id, @PathVariable String hash, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(organizationService.addUser(id, hash, userDetails.getUsername()));
    }

    @PostMapping("/{id}/stations")
    ResponseEntity<NewStationDto> saveStation(@PathVariable Long id, @RequestBody NewStationDto newStation,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        NewStationDto savedStation = organizationService.addStation(newStation, id, userDetails.getUsername());
        URI savedStationUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedStation.getId())
                .toUri();
        return ResponseEntity.created(savedStationUri).body(savedStation);
    }

    @GetMapping("/{id}/stations")
    ResponseEntity<?> viewStations(
            @PathVariable Long id,
            @RequestParam(required = false) boolean onlyAvailable,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) EventType type,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(organizationService.viewStations(id, start, end, type, onlyAvailable, userDetails));
    }

    @PatchMapping("/{organizationId}/stations/{stationId}")
    ResponseEntity<?> updateStation(@RequestBody JsonMergePatch patch,@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable Long stationId, @PathVariable Long organizationId) throws JsonPatchException, JsonProcessingException {
        // JsonPatchException & JsonProcessingException are handled by an ExceptionHandler
        organizationService.updateStation(organizationId,stationId, userDetails.getUsername(), patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{organizationId}/stations/{stationId}")
    ResponseEntity<?> deleteStation(@PathVariable Long stationId,
                                    @PathVariable Long organizationId) {
        organizationService.deleteStation(stationId, organizationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/reservations")
    ResponseEntity<?> addReservations(@RequestBody AddReservationDTO reservationDTO,
            @AuthenticationPrincipal UserDetails userDetails,@PathVariable Long id){
        ReservationDTO reservation = organizationService.addReservation(reservationDTO, id, userDetails.getUsername());
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("{id}/reservations")
    public ResponseEntity<?> viewReservations(@PathVariable Long id,
                                              @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(organizationService.viewReservations(id, userDetails.getUsername()));
    }


    @GetMapping("{id}/users")
    ResponseEntity<Page<?>> viewUsers(@PathVariable Long id,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(organizationService.findUsersByOrganization(id, page, size));
    }

    @GetMapping("/{id}/invite/{email}")
    public ResponseEntity<OrganizationDTO> addUserThroughEmail(@PathVariable Long id, @PathVariable String email){
        return ResponseEntity.ok(organizationService.addUserThroughEmail(id, email));
    }

}
