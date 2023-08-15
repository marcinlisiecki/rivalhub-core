package com.rivalhub.organization.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.service.OrganizationStationService;
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
    private ResponseEntity<StationDTO> saveStation(@PathVariable Long id, @RequestBody StationDTO newStation) {
        StationDTO savedStation = organizationStationService.addStation(newStation, id);
        URI savedStationUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedStation.getId())
                .toUri();
        return ResponseEntity.created(savedStationUri).body(savedStation);
    }

    @GetMapping("/{id}/stations")
    private ResponseEntity<?> viewStations(
            @PathVariable Long id,
            @RequestParam(required = false) boolean onlyAvailable,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false, defaultValue = "false") boolean showInactive) {
        return ResponseEntity.ok(organizationStationService
                .viewStations(id, start, end, type, onlyAvailable, showInactive));
    }

    @GetMapping("/{id}/event-stations")
    private ResponseEntity<List<EventTypeStationsDto>> viewEventStations(@PathVariable Long id,
                                                                 @RequestParam String start,
                                                                 @RequestParam String end,
                                                                 @RequestParam (required = false) EventType type) {
        return ResponseEntity.ok(organizationStationService.getEventStations(id, start, end, type));
    }

    @PatchMapping("/{organizationId}/stations/{stationId}")
    private ResponseEntity<?> updateStation(@RequestBody JsonMergePatch patch,
                                            @PathVariable Long stationId,
                                            @PathVariable Long organizationId) throws JsonPatchException, JsonProcessingException {
        organizationStationService.updateStation(organizationId, stationId, patch);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{organizationId}/stations/{stationId}")
    private ResponseEntity<?> deleteStation(@PathVariable Long stationId,
                                    @PathVariable Long organizationId) {
        organizationStationService.deleteStation(stationId, organizationId);
        return ResponseEntity.noContent().build();
    }

}
