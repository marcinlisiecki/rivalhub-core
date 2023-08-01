package com.rivalhub.organization;

import com.rivalhub.user.UserData;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private OrganizationDTOMapper organizationDTOMapper;

    public OrganizationDTO saveOrganization(OrganizationCreateDTO organizationCreateDTO){
        Organization organizationToSave = organizationDTOMapper.map(organizationCreateDTO);
        organizationToSave.setAddedDate(LocalDateTime.now());

        Organization savedOrganization = organizationRepository.save(organizationToSave);

        createInvitationLink(savedOrganization.getId());
        Organization save = organizationRepository.save(savedOrganization);

        return organizationDTOMapper.map(save);
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

    public String createInvitationLink(Long id) {
        Optional<Organization> organizationById = organizationRepository.findById(id);

        if (organizationById.isEmpty()) return null;

        Organization organization = organizationById.get();
        String valueToHash = organization.getName() + organization.getId() + LocalDateTime.now();
        String hash = String.valueOf(valueToHash.hashCode()  & 0x7fffffff);

        organization.setInvitationLink(hash);
        organizationRepository.save(organization);
        return hash;
    }

    public void addUser(Long id, String hash, UserData userData) {
        Optional<Organization> organizationRepositoryById = organizationRepository.findById(id);

        if (organizationRepositoryById.isEmpty()) return;

        Organization organization = organizationRepositoryById.get();
        if (!organization.getInvitationLink().equals(hash)) return;

        organization.addUser(userData);

        organizationRepository.save(organization);
    }
}
