package com.rivalhub.user;

import com.rivalhub.email.EmailService;
import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationCreateDTO;
import com.rivalhub.organization.OrganizationDTOMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDtoMapper userDtoMapper;
    private final EmailService emailService;


    UserDto register(UserDto userDto) {
        userRepository.findByEmail(userDto.getEmail()).ifPresent(userData -> {throw new UserAlreadyExistsException();});

        UserData user = userDtoMapper.map(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setJoinTime(LocalDateTime.now());
        user.setActivationHash(passwordEncoder.encode(userDto.getEmail())
                .replace("/", "")
                .replace("$", "")
                .replace(".", "")
        );
        user = userRepository.save(user);
        return userDtoMapper.map(user);
    }

    UserDetailsDto findUserById(Long id) {
        return userDtoMapper.mapToUserDetailsDto(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
    }

    List<OrganizationCreateDTO> findOrganizationsByUser(String email) {
        UserData user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        List<Organization> organizationList = user.getOrganizationList();
        List<OrganizationCreateDTO> userOrganizationDTO = organizationList.stream().map(organization -> new OrganizationCreateDTO(organization.getId(),
                        organization.getName(), organization.getImageUrl()))
                .collect(Collectors.toList());

        return userOrganizationDTO;
    }

    @Transactional
    void confirmUserEmail(String hash) {
        UserData user = userRepository.findByActivationHash(hash).orElseThrow(UserNotFoundException::new);
        user.setActivationTime(LocalDateTime.now());
    }


    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    void deleteInactivatedUsers() {
        LocalDateTime deleteTime = LocalDateTime.now().minusDays(1);
        userRepository.deleteInactiveUsers(deleteTime);
    }

    URI sendEmail(UserDto savedUser) {
        URI savedUserUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/users/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        emailService.sendThymeleafInvitation(savedUser,"Activate your account");
        return savedUserUri;
    }
}