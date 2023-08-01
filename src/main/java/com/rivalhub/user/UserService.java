package com.rivalhub.user;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

@Service
public class UserService {
    private UserRepository userRepository;
    int passwordEncodeIteration = 1000;
    Random random;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
        this.random  = new Random();

    }

    public byte[] calculateHashPBKDF2(String password,byte[] salt) {
        byte[] hash = null;
        try {
            SecretKeyFactory sec = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec key = new PBEKeySpec(password.toCharArray(), salt, passwordEncodeIteration, 64 * 8);
            hash = sec.generateSecret(key).getEncoded();
        }
        catch (InvalidKeySpecException e){
            System.out.println("halo1");
        } catch (NoSuchAlgorithmException e){
            System.out.println("halo2");
        }
        return hash;
    }
    public byte[] generateSalt(){
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return bytes;
    }

    public UserDto register(UserDto userDto)  {
        UserData userData = userDtoToUserMapper(userDto);
        byte[] salt = generateSalt();
        byte[] passwordEncoded = calculateHashPBKDF2(userDto.getPassword(), salt);
        userData.setSalt(salt);
        userData.setPasswordHash(passwordEncoded);
        userRepository.save(userData);
        return userDto;

    };


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
