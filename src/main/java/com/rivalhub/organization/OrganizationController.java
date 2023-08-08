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
}
