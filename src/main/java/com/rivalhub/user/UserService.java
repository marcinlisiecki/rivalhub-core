package com.rivalhub.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto register(UserDto userDto)  {
        UserData userData = userDtoToUserMapper(userDto);

        userData.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(userData);

        return userDto;
    }

    public UserData userDtoToUserMapper(UserDto userDto){
        UserData userData = new UserData();
        userData.setName(userDto.getName());
        userData.setEmail(userDto.getEmail());
        userData.setId(userData.getId());
        return userData;
    }

    public UserDtoDetails userToUserDtoDetailsMapper(UserData userData){
        UserDtoDetails userDtoDetails = new UserDtoDetails(userData.getName(), userData.getEmail());
        return userDtoDetails;
    }

    public UserDtoDetails findUserById(Long id){
        return userToUserDtoDetailsMapper(userRepository.findById(id).get());
    }
}
