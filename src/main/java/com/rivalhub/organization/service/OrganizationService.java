package com.rivalhub.organization.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.FileUploadUtil;
import com.rivalhub.common.InvitationHelper;
import com.rivalhub.common.MergePatcher;
import com.rivalhub.common.exception.OrganizationNotFoundException;
import com.rivalhub.event.EventType;
import com.rivalhub.organization.*;
import com.rivalhub.organization.validator.OrganizationSettingsValidator;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final AutoMapper autoMapper;
    private final MergePatcher<OrganizationDTO> organizationMergePatcher;
    private final FileUploadUtil fileUploadUtil;
    private final InvitationHelper invitationHelper;
    private final OrganizationRepoManager organizationRepoManager;
    private final StatsRepository statsRepository;

    public OrganizationDTO saveOrganization(String organizationName, String color, MultipartFile multipartFile){
        Organization organizationToSave = autoMapper.mapToOrganization(
                new OrganizationDTO(organizationName, color));

        var requestUser = SecurityUtils.getUserFromSecurityContext();

        setOrganizationSettings(requestUser, organizationToSave);
        if(multipartFile != null)
            organizationToSave.setImageUrl(fileUploadUtil.saveOrganizationImage(multipartFile, organizationToSave));

        return autoMapper.mapToOrganizationDto(organizationRepository.save(organizationToSave));
    }

     public OrganizationDTO findOrganization(Long id){
        return autoMapper.mapToOrganizationDto(organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new));
    }

    public void deleteOrganization(Long id) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        var organization = organizationRepoManager.getOrganizationWithUsersById(id);

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);
        organizationRepository.delete(organization);
    }

    public String createInvitation(Long id) {
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);
        var requestUser = SecurityUtils.getUserFromSecurityContext();

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);

        organization.setInvitationHash(createInvitationHash(organization));

        organizationRepository.save(organization);
        return invitationHelper.createInvitationLink(organization);
    }

    public void updateOrganization(Long id, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        var organization = organizationRepository.findById(id)
                .orElseThrow(OrganizationNotFoundException::new);

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);

        OrganizationDTO organizationDTO = autoMapper.mapToOrganizationDto(organization);
        OrganizationDTO patchedOrganizationDto = organizationMergePatcher.patch(patch, organizationDTO, OrganizationDTO.class);

        organizationRepository.save(OrganizationMapper.map(patchedOrganizationDto, organization));
    }

    private void setOrganizationSettings(UserData user, Organization organization){
        UserOrganizationService.addAdminUser(user, organization);
        createInvitationForNewOrganization(organization);
        organization.getStats().add(new Stats(user));
    }

    private void createInvitationForNewOrganization(Organization organization){
        organization.setInvitationHash(createInvitationHash(organization));
    }

    private String createInvitationHash(Organization organization){
        String valueToHash = organization.getName() + LocalDateTime.now();
        return String.valueOf(valueToHash.hashCode() & 0x7fffffff);
    }

    public Object saveCustomImage(MultipartFile multipartFile,String keepAvatar, Long id) {
        boolean deleteAvatar = !Boolean.parseBoolean(keepAvatar);

        Organization organization = organizationRepository.findById(id).orElseThrow(OrganizationNotFoundException::new);
        var requestUser = SecurityUtils.getUserFromSecurityContext();

        OrganizationSettingsValidator.checkIfUserIsAdmin(requestUser, organization);
        fileUploadUtil.updateOrganizationImage(multipartFile,deleteAvatar, organization);

        return autoMapper.mapToOrganizationDto(organizationRepository.save(organization));
    }

    public List<StatsDTO> getStatsByType(Long id, EventType type) {
        List<Stats> allStatsByOrganization = statsRepository.findAllByOrganizationId(id);
        List<StatsDTO> statsDTOS = new ArrayList<>();

        if(type.equals(EventType.BILLIARDS)){
            allStatsByOrganization.forEach(stats -> {
                statsDTOS.add(new StatsDTO(autoMapper.mapToUserDisplayDTO(stats.getUserData()), stats.getGamesInBilliards(), stats.getWinInBilliards()));
            });
        }else if(type.equals(EventType.PING_PONG)) {
            allStatsByOrganization.forEach(stats -> {
                statsDTOS.add(new StatsDTO(autoMapper.mapToUserDisplayDTO(stats.getUserData()), stats.getGamesInPingPong(), stats.getWinInPingPong()));
            });
        }else if(type.equals(EventType.PULL_UPS)) {
            allStatsByOrganization.forEach(stats -> {
                statsDTOS.add(new StatsDTO(autoMapper.mapToUserDisplayDTO(stats.getUserData()), stats.getGamesInPullUps(), stats.getWinInPullUps()));
            });
        }else if(type.equals(EventType.TABLE_FOOTBALL)) {
            allStatsByOrganization.forEach(stats -> {
                statsDTOS.add(new StatsDTO(autoMapper.mapToUserDisplayDTO(stats.getUserData()), stats.getGamesInTableFootBall(), stats.getWinInTableFootBall()));
            });
        }else if(type.equals(EventType.DARTS)) {
            allStatsByOrganization.forEach(stats -> {
                statsDTOS.add(new StatsDTO(autoMapper.mapToUserDisplayDTO(stats.getUserData()), stats.getGamesInDarts(), stats.getWinInDarts()));
            });
        }else if (type.equals(EventType.RUNNING)){
            allStatsByOrganization.forEach(stats -> {
                statsDTOS.add(new StatsDTO(autoMapper.mapToUserDisplayDTO(stats.getUserData()), stats.getGamesInRunning(), stats.getWinInRunning()));
            });
        }
    return statsDTOS;
    }
}
