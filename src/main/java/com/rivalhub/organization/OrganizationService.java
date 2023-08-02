package com.rivalhub.organization;

import com.rivalhub.station.NewStationDto;
import com.rivalhub.station.NewStationDtoMapper;
import com.rivalhub.station.Station;
import com.rivalhub.station.StationRepository;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationDTOMapper organizationDTOMapper;

    private final UserRepository userRepository;

    private final NewStationDtoMapper newStationDtoMapper;

    private final StationRepository stationRepository;


        public OrganizationDTO saveOrganization(OrganizationCreateDTO organizationCreateDTO, String email){
        Organization organizationToSave = organizationDTOMapper.map(organizationCreateDTO);
        organizationToSave.setAddedDate(LocalDateTime.now());

        Organization savedOrganization = organizationRepository.save(organizationToSave);

        createInvitationLink(savedOrganization.getId());
        UserData user = userRepository.findByEmail(email).get();
        savedOrganization.addUser(user);
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

    public Optional<Organization> addUser(Long id, String hash, String email) {
        Optional<Organization> organizationRepositoryById = organizationRepository.findById(id);
        Optional<UserData> user = userRepository.findByEmail(email);

        if(user.isEmpty()) return Optional.empty();
        if (organizationRepositoryById.isEmpty()) return Optional.empty();

        Organization organization = organizationRepositoryById.get();
        if (!organization.getInvitationLink().equals(hash)) return Optional.empty();

        organization.addUser(user.get());

        return Optional.of(organizationRepository.save(organization));
    }

    NewStationDto addStation(NewStationDto newStationDto, Long id, String email) {
        Optional<Organization> organizationOptional = organizationRepository.findById(id);
        if (organizationOptional.isEmpty()) return null;
        Organization organization = organizationOptional.get();

        UserData user = userRepository.findByEmail(email).get();
        List<Organization> organizationList = user.getOrganizationList();

        Organization currentOrganization = organizationList.stream().filter(org -> org.getId().equals(id)).findFirst().orElse(null);

        if (currentOrganization == null) return null;

        Station station = newStationDtoMapper.map(newStationDto);
        Station savedStation = stationRepository.save(station);
        organization.addStation(savedStation);

        organizationRepository.save(organization);

        return newStationDtoMapper.map(savedStation);
    }



}
