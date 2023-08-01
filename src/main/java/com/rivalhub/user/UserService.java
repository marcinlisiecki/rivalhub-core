package com.rivalhub.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDtoMapper userDtoMapper;
    private final UserDtoDetailsMapper userDtoDetailsMapper;

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
}
