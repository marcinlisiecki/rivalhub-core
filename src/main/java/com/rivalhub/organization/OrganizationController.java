package com.rivalhub.organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.email.EmailService;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final EmailService emailService;
    private final UserRepository userRepository;


    @GetMapping("{id}")
    public ResponseEntity<Optional<OrganizationDTO>> viewOrganization(@PathVariable Long id){
        if (organizationService.findOrganization(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
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
    @GetMapping("/{id}/invite/{email}")
    public ResponseEntity<?> addUserThroughEmail(@PathVariable Long id, @PathVariable String email, @AuthenticationPrincipal UserDetails userDetails){
        if(userDetails == null) return ResponseEntity.notFound().build();
        Optional<OrganizationDTO> organizationDTO = organizationService.findOrganization(id);
        if(organizationDTO.isEmpty()) return ResponseEntity.notFound().build();
        StringBuilder builder = new StringBuilder("Invitation to ") .append(organizationDTO.get().getName());
        String subject = builder.toString();
        builder.setLength(0);
        ServletUriComponentsBuilder uri = ServletUriComponentsBuilder.fromCurrentRequest();
        uri.replacePath("");
        builder.append("Enter the link to join: \n")
                .append(uri.toUriString()).append("/")
                .append(organizationDTO.get().getId())
                .append("/invitation/")
                .append(organizationDTO.get().getInvitationHash());
        String body = builder.toString();
        emailService.sendSimpleMessage(email, subject, body);
        return ResponseEntity.ok(organizationDTO.toString());
    }


}
