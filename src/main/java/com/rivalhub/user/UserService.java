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
    private final UserDtoDetailsMapper userDtoDetailsMapper;
    private final EmailService emailService;


    public UserDto register(UserDto userDto) {
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

    public UserDetailsDto findUserById(Long id) {
        return userDtoDetailsMapper.map(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
    }

    public List<OrganizationCreateDTO> findOrganizationsByUser(String email) {
        UserData user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);

        List<Organization> organizationList = user.getOrganizationList();
        List<OrganizationCreateDTO> userOrganizationDTO = organizationList.stream().map(organization -> new OrganizationCreateDTO(organization.getId(),
                        organization.getName(), organization.getImageUrl()))
                .collect(Collectors.toList());

        return userOrganizationDTO;
    }

    @Transactional
    public void confirmUserEmail(String hash) {
        UserData user = userRepository.findByActivationHash(hash).orElseThrow(UserNotFoundException::new);
        user.setActivationTime(LocalDateTime.now());
    }

    public String createActivationLink(UserDto userDto) {
        StringBuilder builder = new StringBuilder();
        builder.setLength(0);
        ServletUriComponentsBuilder uri = ServletUriComponentsBuilder.fromCurrentRequest();
        uri.replacePath("");
        builder.append("Enter the link to join: \n")
                .append(uri.toUriString())
                .append("/confirm/")
                .append(userDto.getActivationHash());
        String body = builder.toString();
        return body;
    }

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void deleteInactivatedUsers() {
        LocalDateTime deleteTime = LocalDateTime.now().minusDays(1);
        userRepository.deleteInactiveUsers(deleteTime);
    }

    public URI sendEmail(UserDto savedUser) {
        URI savedUserUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/users/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        emailService.sendSimpleMessage(savedUser.getEmail(),
                "Welcome on RivalHub",
                "Your account was successfully created\n Activate your account: " + createActivationLink(savedUser));
        return savedUserUri;
    }
}
