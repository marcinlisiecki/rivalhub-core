package com.rivalhub.user.profile;

import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;

public class UserMapper {
    public static UserDetailsDto map(UserData userData){
        UserDetailsDto userDetailsDto = new UserDetailsDto();

        userDetailsDto.setName(userData.getName());
        userDetailsDto.setId(userData.getId());
        userDetailsDto.setProfilePictureUrl(userData.getProfilePictureUrl());
        userDetailsDto.setEmail(userData.getEmail());

        return userDetailsDto;
    }

    public static UserData mapUserDetailsDtoToUserData(UserDetailsDto userDetailsDto, UserData userData){
        userData.setName(userDetailsDto.getName());
        userData.setId(userDetailsDto.getId());
        userData.setProfilePictureUrl(userDetailsDto.getProfilePictureUrl());
        userData.setEmail(userDetailsDto.getEmail());
        return userData;
    }
}
