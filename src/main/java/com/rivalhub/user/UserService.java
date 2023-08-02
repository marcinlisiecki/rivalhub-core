package com.rivalhub.user;

import com.rivalhub.organization.Organization;
import com.rivalhub.organization.OrganizationCreateDTO;
import com.rivalhub.organization.OrganizationDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
            userData = userRepository.save(userData);
            return userDtoMapper.map(userData);
        } else {
            // TODO: Throw UserAlreadyExistsException
            return null;
        }
    }

    public UserDtoDetails findUserById(Long id) {
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

}
