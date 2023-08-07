package com.rivalhub.organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.common.MergePatcher;
import com.rivalhub.event.EventType;
import com.rivalhub.reservation.AddReservationDTO;
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
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    private final MergePatcher<OrganizationDTO> organizationMergePatcher;
    private final MergePatcher<NewStationDto> stationMergePatcher;

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
        // JsonPatchException & JsonProcessingException are handled by an ExceptionHandler

        OrganizationDTO organizationDTO = organizationService.findOrganization(id);
        OrganizationDTO patchedOrganizationDto = organizationMergePatcher.patch(patch, organizationDTO, OrganizationDTO.class);
        patchedOrganizationDto.setId(id);
        organizationService.updateOrganization(patchedOrganizationDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/invitation")
    public ResponseEntity<?> createInvitation(@PathVariable Long id){
        String invitationHash = organizationService.createInvitationHash(id);
        if(invitationHash == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(invitationHash);
    }

    @GetMapping("/{id}/invitation/{hash}")
    public ResponseEntity<?> addUser(@PathVariable Long id, @PathVariable String hash, @AuthenticationPrincipal UserDetails userDetails){
        Organization organization = organizationService.addUser(id, hash, userDetails.getUsername());
        return ResponseEntity.ok(organization.toString());
    }

    @PostMapping("/{id}/stations")
    ResponseEntity<NewStationDto> saveStation(@PathVariable Long id, @RequestBody NewStationDto newStation,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        NewStationDto savedStation = organizationService.addStation(newStation, id, userDetails.getUsername());

        if (savedStation == null) {
            return ResponseEntity.notFound().build();
        }

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
            @RequestParam(required = false) EventType type) {

        if (onlyAvailable && start != null && end != null) {
            List<Station> availableStations = organizationService
                    .getAvailableStations(id, start, end, type);

            return ResponseEntity.ok(availableStations);
        }
        List<Station> stations = organizationService.findStations(id, userDetails.getUsername());

        return ResponseEntity.ok(stations);
    }

    @PatchMapping("/{organizationId}/stations/{stationId}")
    ResponseEntity<?> updateOrganization(@RequestBody JsonMergePatch patch,
                                         @PathVariable Long stationId) throws JsonPatchException, JsonProcessingException {
        // JsonPatchException & JsonProcessingException are handled by an ExceptionHandler

        NewStationDto station = organizationService.findStation(organizationId, stationId, userDetails.getUsername());
        NewStationDto stationPatched = stationMergePatcher.patch(patch, station, NewStationDto.class);
        stationPatched.setId(stationId);
        organizationService.updateStation(stationPatched);

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

    @GetMapping("{id}/users")
    ResponseEntity<Page<?>> viewUsers(@PathVariable Long id,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(organizationService.findUsersByOrganization(id, page, size));
    }

    @GetMapping("/{id}/invite/{email}")
    public ResponseEntity<OrganizationDTO> addUserThroughEmail(@PathVariable Long id, @PathVariable String email){
        OrganizationDTO organizationDTO = organizationService.addUserThroughEmail(id, email);
        return ResponseEntity.ok(organizationDTO);
    }
}
