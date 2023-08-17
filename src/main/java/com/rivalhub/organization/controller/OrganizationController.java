package com.rivalhub.organization.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.organization.service.OrganizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
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
    private ResponseEntity<?> addOrganization(@RequestParam("organization") String organizationJson, @RequestParam("thumbnail") MultipartFile multipartFile) throws IOException {
//        OrganizationDTO savedOrganization = organizationService.saveOrganization(organizationDTO);

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        String uploadDir = "organization-photos/";

        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        return ResponseEntity.ok(null);

//        URI savedOrganizationUri = ServletUriComponentsBuilder.fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(savedOrganization.getId())
//                .toUri();
//        return ResponseEntity.created(savedOrganizationUri).body(savedOrganization);
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
}


//@RestController
//@RequestMapping("/api/organizations")
//public class OrganizationController {
//
//    private static final String UPLOAD_DIR = "uploads"; // Katalog, gdzie będą przechowywane przesłane pliki
//
//    @Autowired
//    private OrganizationService organizationService;
//
//    @PostMapping("/add")
//    public ResponseEntity<Organization> addOrganization(
//            @RequestParam("thumbnail") MultipartFile thumbnail,
//            @RequestParam("organization") String organizationJson) {
//        try {
//            // Konwersja JSON na obiekt Organization
//            ObjectMapper objectMapper = new ObjectMapper();
//            Organization organization = objectMapper.readValue(organizationJson, Organization.class);
//
//            // Zapis pliku na serwerze
//            if (!thumbnail.isEmpty()) {
//                String uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().toString();
//                String filename = StringUtils.cleanPath(thumbnail.getOriginalFilename());
//                Path filePath = Paths.get(uploadPath, filename);
//                Files.copy(thumbnail.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//                organization.setThumbnailPath(filename);
//            }
//
//            // Wywołanie metody serwisu do dodania organizacji
//            Organization addedOrganization = organizationService.add(organization);
//
//            return ResponseEntity.ok(addedOrganization);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//}