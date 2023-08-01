package com.rivalhub.user;

import org.springframework.stereotype.Service;

@Service
public class UserDtoDetailsMapper {
    UserDtoDetails map(UserData userData){
        UserDtoDetails userDtoDetails = new UserDtoDetails();
        userDtoDetails.setId(userData.getId());
        userDtoDetails.setEmail(userData.getEmail());
        userDtoDetails.setName(userData.getName());
        return userDtoDetails;
    }
    UserData map(UserDtoDetails userDtoDetails){
        UserData user = new UserData();
        user.setId(userDtoDetails.getId());
        user.setEmail(userDtoDetails.getEmail());
        user.setName(userDtoDetails.getName());
        return user;

    }

}
