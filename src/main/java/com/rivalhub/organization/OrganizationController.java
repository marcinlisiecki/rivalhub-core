package com.rivalhub.organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import com.rivalhub.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/organizations")
@AllArgsConstructor
public class OrganizationController {

    private OrganizationService organizationService;
    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    @GetMapping("{id}")
    public ResponseEntity<Optional<OrganizationDTO>> viewOrganization(@PathVariable Long id){
        if (organizationService.findOrganization(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(organizationService.findOrganization(id));
    }

    @PostMapping
    public ResponseEntity<OrganizationDTO> addOrganization(@RequestBody OrganizationCreateDTO organizationCreateDTO){
        OrganizationDTO savedOrganization = organizationService.saveOrganization(organizationCreateDTO);
        URI savedOrganizationUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedOrganization.getId())
                .toUri();
        return ResponseEntity.created(savedOrganizationUri).body(savedOrganization);
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateJobOffer(@PathVariable Long id, @RequestBody JsonMergePatch patch) {
        try {
            OrganizationDTO jobOffer = organizationService.findOrganization(id).orElseThrow();
            OrganizationDTO offerPatched = applyPatch(jobOffer, patch);
            organizationService.updateOrganization(offerPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private OrganizationDTO applyPatch(OrganizationDTO jobOffer, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode jobOfferNode = objectMapper.valueToTree(jobOffer);
        JsonNode jobOfferPatchedNode = patch.apply(jobOfferNode);
        return objectMapper.treeToValue(jobOfferPatchedNode, OrganizationDTO.class);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteJobOffer(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/invitation")
    public String createInvitation(@PathVariable Long id){
        String invitationLink = organizationService.createInvitationLink(id);
        System.out.println(invitationLink);
        return invitationLink;
    }

    @GetMapping("/{id}/invitation/{hash}")
    public String addUser(@PathVariable Long id, @PathVariable String hash, @AuthenticationPrincipal UserDetails userDetails){
        if (userDetails == null) return null;

        System.out.println(userDetails.toString());

        UserData userData = new UserData(userDetails.getUsername());
        UserData save = userRepository.save(userData);
        organizationService.addUser(id, hash, save);

        return "";
    }

}
