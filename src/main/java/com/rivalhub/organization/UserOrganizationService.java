package com.rivalhub.organization;


import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;

public class UserOrganizationService {


    public static void addUser(UserData userData, Organization organization){
        userData.getOrganizationList().add(organization);
        organization.getUserList().add(userData);
    }

    public static void addStation(Station station, Organization organization){
        organization.getStationList().add(station);
    }

    public static void removeStation(Station station, Organization organization){
        organization.getStationList().remove(station);
    }
}
