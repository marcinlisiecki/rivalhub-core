package com.rivalhub.user;

import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationCreateDTO;
import com.rivalhub.organization.OrganizationDTOMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    private final OrganizationDTOMapper organizationDTOMapper;

    public UserDto register(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isEmpty()) {
            UserData userData = userDtoMapper.map(userDto);
            userData.setPassword(passwordEncoder.encode(userDto.getPassword()));
            userData.setJoinTime(LocalDateTime.now());

            userData.setActivationHash(passwordEncoder.encode(userDto.getEmail())
                    .replace("/","")
                    .replace("$","")
                    .replace(".","")
            );
            userData = userRepository.save(userData);
            return userDtoMapper.map(userData);
        } else {
            // TODO: Throw UserAlreadyExistsException
            return null;
        }
    }

    public UserDetailsDto findUserById(Long id) {
        return userDtoDetailsMapper.map(userRepository.findById(id).get());
    }

    public Optional<List<OrganizationCreateDTO>> findOrganizationsByUser(String email){
        Optional<UserData> user = userRepository.findByEmail(email);

        if (user.isEmpty()) return Optional.empty();

        List<Organization> organizationList = user.get().getOrganizationList();
        List<OrganizationCreateDTO> userOrganizationDTO = organizationList.stream().map(organization -> new OrganizationCreateDTO(organization.getId(),
                        organization.getName(), organization.getImageUrl()))
                .collect(Collectors.toList());

        return Optional.of(userOrganizationDTO);
    }

    @Transactional
    public boolean confirmUserEmail(String hash){
        Optional<UserData> user = userRepository.findByActivationHash(hash);
        if(user.isEmpty()){
            return false;
        } else{
            user.get().setActivationTime(LocalDateTime.now());
            return true;
        }
    }

    public String createActivationLink(UserDto userDto){
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
    public void deleteInactivatedUsers(){
        System.out.println("Usuwanko");
        LocalDateTime deleteTime =LocalDateTime.now().minusDays(1);
        userRepository.deleteInactiveUsers(deleteTime);
    }

}
