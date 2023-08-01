package com.rivalhub.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserDtoMapper userDtoMapper;
    private UserDtoDetailsMapper userDtoDetailsMapper;
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder,
                       UserDtoMapper userDtoMapper,UserDtoDetailsMapper userDtoDetailsMapper){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDtoMapper = userDtoMapper;
        this.userDtoDetailsMapper = userDtoDetailsMapper;
    }

    public UserDto register(UserDto userDto)  {
        if(userRepository.findByEmail(userDto.getEmail()) == null) {

            UserData userData = userDtoMapper.map(userDto);
            userData.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
            userData = userRepository.save(userData);
            return userDtoMapper.map(userData);
        } else return null;

    };



    public UserDtoDetails findUserById(Long id){
        return userDtoDetailsMapper.map(userRepository.findById(id).get());
    }
}
