package com.rivalhub.organization.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.organization.service.OrganizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;



@RestController
@RequestMapping("/organizations")
@AllArgsConstructor
public class OrganizationController {
    private OrganizationService organizationService;
    @GetMapping("{id}")
    private ResponseEntity<OrganizationDTO> viewOrganization(@PathVariable Long id){
        return ResponseEntity.ok(organizationService.findOrganization(id));
    }

    @PostMapping
    private ResponseEntity<OrganizationDTO> addOrganization(@RequestParam("organization") String organizationJson,
                                                            @RequestParam("color") String color,
                                                            @RequestParam(name = "thumbnail", required = false) MultipartFile multipartFile) {
        OrganizationDTO savedOrganization = organizationService.saveOrganization(organizationJson, color, multipartFile);
        URI savedOrganizationUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedOrganization.getId())
                .toUri();
        return ResponseEntity.created(savedOrganizationUri).body(savedOrganization);
    }

    @PostMapping("{id}/image")
    private ResponseEntity<?> saveCustomImage(@RequestParam(name = "thumbnail", required = false) MultipartFile multipartFile,
                                              @RequestParam(name = "keepAvatar", required = false) String keepAvatar,
                                              @PathVariable Long id){
        return ResponseEntity.ok(organizationService.saveCustomImage(multipartFile,keepAvatar, id));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void updateOrganization(@PathVariable Long id, @RequestBody JsonMergePatch patch)
            throws JsonPatchException, JsonProcessingException {
        organizationService.updateOrganization(id, patch);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
    }


    @PostMapping("/{id}/invitation")
    private ResponseEntity<?> createInvitation(@PathVariable Long id){
        return ResponseEntity.ok(organizationService.createInvitation(id));
    }

    @GetMapping("/{id}/stats")
    private ResponseEntity<?> getStatsByType(@PathVariable Long id, @RequestParam EventType type){
        return ResponseEntity.ok(organizationService.getStatsByType(id, type));
    }
}