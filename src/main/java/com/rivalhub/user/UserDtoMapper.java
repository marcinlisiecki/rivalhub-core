package com.rivalhub.user;

import org.springframework.stereotype.Service;

@Service
public class UserDtoMapper {
    UserData map(UserDto userDto){
        UserData userData = new UserData();
        userData.setName(userDto.getName());
        userData.setEmail(userDto.getEmail());
        userData.setId(userData.getId());
        userData.setActivationHash(userDto.getActivationHash());
        return userData;
    }

    UserDto map(UserData userData){
        UserDto userDto = new UserDto();
        userDto.setEmail(userData.getEmail());
        userDto.setId(userData.getId());
        userDto.setPassword(userData.getPassword());
        userDto.setName(userData.getName());
        userDto.setActivationHash(userData.getActivationHash());
        return userDto;
    }


}
