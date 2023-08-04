package com.rivalhub.organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.reservation.AddReservationDTO;
import com.rivalhub.reservation.Reservation;
import com.rivalhub.station.NewStationDto;
import com.rivalhub.station.Station;
import com.rivalhub.user.*;
import com.rivalhub.email.EmailService;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/organizations")
@AllArgsConstructor
public class OrganizationController {

    private OrganizationService organizationService;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;
    private final UserRepository userRepository;

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
    ResponseEntity<?> updateOrganization(@PathVariable Long id, @RequestBody JsonMergePatch patch) {
        try {
            OrganizationDTO organizationDTO = organizationService.findOrganization(id).orElseThrow();
            OrganizationDTO offerPatched = applyPatch(organizationDTO, patch);
            organizationService.updateOrganization(offerPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private OrganizationDTO applyPatch(OrganizationDTO organizationDTO, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode organizationNode = objectMapper.valueToTree(organizationDTO);
        JsonNode organizationPatchedNode = patch.apply(organizationNode);
        return objectMapper.treeToValue(organizationPatchedNode, OrganizationDTO.class);
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
        if (userDetails == null) return ResponseEntity.notFound().build();

        Optional<Organization> organization = organizationService.addUser(id, hash, userDetails.getUsername());
        if (organization.isEmpty()) return ResponseEntity.notFound().build();

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
    ResponseEntity<?> viewStations(@PathVariable Long id){
        Optional<List<Station>> stations = organizationService.findStations(id);

        if (stations.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(stations.get());
    }

    @PatchMapping("/stations/{stationId}")
    ResponseEntity<?> updateOrganization(@RequestBody JsonMergePatch patch,
                                         @PathVariable Long stationId) {
        try {
            NewStationDto station = organizationService.findStation(stationId).orElseThrow();
            NewStationDto stationPatched = applyPatch(station, patch);
            organizationService.updateStation(stationPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private NewStationDto applyPatch(NewStationDto station, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode stationNode = objectMapper.valueToTree(station);
        JsonNode stationPatchedNode = patch.apply(stationNode);
        return objectMapper.treeToValue(stationPatchedNode, NewStationDto.class);
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

        Optional<?> reservation = organizationService.addReservation(reservationDTO, id, userDetails.getUsername());

        if (reservation.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(reservation);
    }


    @GetMapping("{id}/users")
    ResponseEntity<Page<?>> viewUsers(@PathVariable Long id,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size){
        if (organizationService.findOrganization(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(organizationService.findUsersByOrganization(id, page, size));
    }


    @GetMapping("/{id}/invite/{email}")
    public ResponseEntity<OrganizationDTO> addUserThroughEmail(@PathVariable Long id, @PathVariable String email, @AuthenticationPrincipal UserDetails userDetails){
        if(userDetails == null) return ResponseEntity.notFound().build();
        OrganizationDTO organizationDTO = organizationService.findOrganization(id);
        String subject = "Invitation to " + organizationDTO.getName();
        String body = organizationService.createInvitationLink(organizationDTO);
        emailService.sendSimpleMessage(email, subject, body);
        return ResponseEntity.ok(organizationDTO);
    }
}
