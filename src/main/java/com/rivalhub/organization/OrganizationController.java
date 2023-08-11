package com.rivalhub.organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

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
    ResponseEntity<?> updateOrganization(@PathVariable Long id, @RequestBody JsonMergePatch patch,
                                         @AuthenticationPrincipal UserDetails userDetails)
            throws JsonPatchException, JsonProcessingException {
        organizationService.updateOrganization(id, patch, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/invitation")
    public ResponseEntity<?> createInvitation(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(organizationService.createInvitation(id, userDetails.getUsername()));
    }

    @GetMapping("/{id}/invitation")
    public ResponseEntity<?> viewInvitation(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(organizationService.viewInvitationLink(id, userDetails.getUsername()));
    }

}
