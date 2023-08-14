package com.rivalhub.user;

import com.rivalhub.auth.AuthService;
import com.rivalhub.auth.JwtTokenDto;
import com.rivalhub.auth.LoginRequestDto;
import com.rivalhub.common.AutoMapper;
import com.rivalhub.email.EmailService;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.organization.RepositoryManager;
import com.rivalhub.user.profile.UserProfileHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserProfileHelper userProfileHelper;
    private final RepositoryManager repositoryManager;
    private final PasswordEncoder passwordEncoder;
    private final AutoMapper autoMapper;
    private final EmailService emailService;
    private final AuthService authService;

    JwtTokenDto register(RegisterRequestDto registerRequestDto) {
        repositoryManager.findByEmail(registerRequestDto.getEmail()).ifPresent(userData -> {
            throw new UserAlreadyExistsException();
        });

        UserData user = autoMapper.mapToUserData(registerRequestDto);
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setJoinTime(LocalDateTime.now());
        user.setActivationHash(passwordEncoder.encode(registerRequestDto.getEmail())
                .replace("/", "")
                .replace("$", "")
                .replace(".", "")
        );
        user = repositoryManager.save(user);
        sendEmail(autoMapper.mapToUserDto(user));

        LoginRequestDto loginRequestDto = autoMapper.mapToLoginRequest(registerRequestDto);
        return authService.login(loginRequestDto);
    }

    UserDetailsDto findUserById(Long id) {
        return autoMapper.mapToUserDetails(repositoryManager.findUserById(id));
    }

    UserDetailsDto getMe(UserDetails userDetails) {
        return autoMapper.mapToUserDetails(repositoryManager
                .findUserByEmail(userDetails.getUsername()));
    }

    List<OrganizationDTO> findOrganizationsByUser(String email) {
        UserData user = repositoryManager.findUserByEmail(email);

        List<Organization> organizationList = user.getOrganizationList();
        List<OrganizationDTO> userOrganizationDTO = organizationList.stream().map(organization -> new OrganizationDTO(organization.getId(),
                        organization.getName(), organization.getImageUrl()))
                .collect(Collectors.toList());

        return userOrganizationDTO;
    }

    @Transactional
    void confirmUserEmail(String hash) {
        UserData user = repositoryManager.findByActivationHash(hash);
        user.setActivationTime(LocalDateTime.now());
    }


    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    void deleteInactivatedUsers() {
        LocalDateTime deleteTime = LocalDateTime.now().minusDays(1);
        repositoryManager.deleteInactiveUsers(deleteTime);
    }

    URI sendEmail(UserDto savedUser) {
        URI savedUserUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/users/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        emailService.sendThymeleafInvitation(savedUser, "Activate your account");
        return savedUserUri;
    }

}
