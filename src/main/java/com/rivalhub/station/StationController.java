package com.rivalhub.station;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    ResponseEntity<NewStationDto> saveStation(@Valid @RequestBody NewStationDto newStation) {
        NewStationDto savedStation = stationService.addStation(newStation);
        URI savedStationUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedStation.getId())
                .toUri();
        return ResponseEntity.created(savedStationUri).body(savedStation);
    }
}
