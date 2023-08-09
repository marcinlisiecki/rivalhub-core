package com.rivalhub.user;

import org.springframework.stereotype.Service;

@Service
public class UserDtoDetailsMapper {
    UserDetailsDto map(UserData userData){
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        userDetailsDto.setId(userData.getId());
        userDetailsDto.setEmail(userData.getEmail());
        userDetailsDto.setName(userData.getName());
        return userDetailsDto;
    }
    UserData map(UserDetailsDto userDetailsDto){
        UserData user = new UserData();
        user.setId(userDetailsDto.getId());
        user.setEmail(userDetailsDto.getEmail());
        user.setName(userDetailsDto.getName());
        return user;
    }

    public UserDetailsDto mapUserToReservationDTO(UserData user){
        UserDetailsDto userReservationDTO = new UserDetailsDto();

        userReservationDTO.setProfilePictureUrl(user.getProfilePictureUrl());
        userReservationDTO.setName(user.getName());
        userReservationDTO.setEmail(user.getEmail());

        return userReservationDTO;
    }

}
