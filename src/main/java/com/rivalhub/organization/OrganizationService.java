package com.rivalhub.organization;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private OrganizationDTOMapper organizationDTOMapper;

    public OrganizationDTO saveOrganization(OrganizationDTO organizationDTO){
        Organization organizationToSave = organizationDTOMapper.map(organizationDTO);
        organizationToSave.setAddedDate(LocalDateTime.now());

        Organization savedOrganization = organizationRepository.save(organizationToSave);
        return organizationDTOMapper.map(savedOrganization);
    }

    public Optional<OrganizationDTO> findOrganization(Long id){
        return organizationRepository.findById(id).map(organizationDTOMapper::map);
    }

    void updateOrganization(OrganizationDTO organizationDTO){
        Organization organization = organizationDTOMapper.map(organizationDTO);
        organizationRepository.save(organization);
    }

    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }

}
