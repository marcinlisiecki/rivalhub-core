package com.rivalhub.user;

import com.rivalhub.auth.AuthService;
import com.rivalhub.auth.JwtTokenDto;
import com.rivalhub.auth.LoginRequestDto;
import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.exception.UserAlreadyActivated;
import com.rivalhub.common.exception.UserAlreadyExistsException;
import com.rivalhub.common.exception.UserNotFoundException;
import com.rivalhub.email.EmailService;
import com.rivalhub.organization.OrganizationDTO;
import com.rivalhub.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AutoMapper autoMapper;
    private final EmailService emailService;
    private final AuthService authService;

    JwtTokenDto register(RegisterRequestDto registerRequestDto) {
        userRepository.findByEmail(registerRequestDto.getEmail()).ifPresent(userData -> {
            throw new UserAlreadyExistsException();
        });
        saveUserAndSandMail(registerRequestDto);

        LoginRequestDto loginRequestDto = autoMapper.mapToLoginRequest(registerRequestDto);
        return authService.login(loginRequestDto);
    }

    UserDetailsDto findUserById(Long id) {
        return autoMapper.mapToUserDetails(userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new));
    }

    UserDetailsDto getMe() {
        return autoMapper.mapToUserDetails(SecurityUtils.getUserFromSecurityContext());
    }

    List<OrganizationDTO> findOrganizationsByRequestUser() {
        return userRepository.getOrganizationsByUserId(SecurityUtils.getUserFromSecurityContext().getId())
                .stream().map(u -> OrganizationDTO.builder()
                        .id(u.get(0, Long.class))
                        .name(u.get(1, String.class))
                        .imageUrl(u.get(2, String.class))
                        .colorForDefaultImage(u.get(3, String.class))
                        .build())
                        .collect(Collectors.toList());
    }

    @Transactional
    public void confirmUserEmail(String hash) {
        UserData user = userRepository.findByActivationHash(hash)
                .orElseThrow(UserNotFoundException::new);
        if (user.getActivationTime() != null) throw new UserAlreadyActivated();
        user.setActivationTime(LocalDateTime.now());
    }


    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void deleteInactivatedUsers() {
        LocalDateTime deleteTime = LocalDateTime.now().minusDays(1);
        userRepository.deleteInactiveUsers(deleteTime);
    }

    private URI sendEmail(UserDto savedUser) {
        URI savedUserUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/users/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        emailService.sendThymeleafInvitation(savedUser, "Aktywuj konto");
        return savedUserUri;
    }

    private void saveUserAndSandMail(RegisterRequestDto registerRequestDto) {
        UserData user = autoMapper.mapToUserData(registerRequestDto);
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setJoinTime(LocalDateTime.now());
        user.setActivationHash(passwordEncoder.encode(registerRequestDto.getEmail())
                .replace("/", "")
                .replace("$", "")
                .replace(".", "")
        );
        user = userRepository.save(user);
        sendEmail(autoMapper.mapToUserDto(user));
    }
}
