package com.rivalhub.organization;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.event.EventType;
import com.rivalhub.station.EventTypeStationsDto;
import com.rivalhub.station.StationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationStationController {
    private final OrganizationStationService organizationStationService;

    @PostMapping("/{id}/stations")
    ResponseEntity<StationDTO> saveStation(@PathVariable Long id, @RequestBody StationDTO newStation,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        StationDTO savedStation = organizationStationService.addStation(newStation, id, userDetails.getUsername());
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
            @RequestParam(required = false, defaultValue = "false")
            boolean showInactive,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(organizationStationService
                .viewStations(id, start, end, type, onlyAvailable, userDetails.getUsername(), showInactive));
    }

    @GetMapping("/{id}/event-stations")
    ResponseEntity<List<EventTypeStationsDto>> viewEventStations(@PathVariable Long id,
                                                                 @RequestParam String start,
                                                                 @RequestParam String end,
                                                                 @RequestParam EventType type) {

        return ResponseEntity.ok(organizationStationService.getEventStations(id, start, end, type));
    }

    @PatchMapping("/{organizationId}/stations/{stationId}")
    ResponseEntity<?> updateStation(@RequestBody JsonMergePatch patch, @AuthenticationPrincipal UserDetails userDetails,
                                    @PathVariable Long stationId, @PathVariable Long organizationId) throws JsonPatchException, JsonProcessingException {

        organizationStationService.updateStation(organizationId, stationId, userDetails.getUsername(), patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{organizationId}/stations/{stationId}")
    ResponseEntity<?> deleteStation(@PathVariable Long stationId,
                                    @PathVariable Long organizationId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        organizationStationService.deleteStation(stationId, organizationId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

}
