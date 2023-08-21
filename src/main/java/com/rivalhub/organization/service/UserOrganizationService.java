package com.rivalhub.organization.service;


import com.rivalhub.event.EventType;
import com.rivalhub.organization.Organization;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserOrganizationService {
    public static void addUser(UserData userData, Organization organization){
        organization.getUserList().add(userData);
    }

    public static void addAdminUser(UserData userData, Organization organization){
        organization.getUserList().add(userData);
        organization.getAdminUsers().add(userData);
    }

    public static void addStation(Station station, Organization organization){
        organization.getStationList().add(station);
    }

    public static void removeEventType(Organization organization, List<EventType> eventTypes) {
        eventTypes.forEach(
                organization.getEventTypeInOrganization()::remove);
    }

    public static void addEventType(Organization organization, List<EventType> eventTypes) {
        organization.getEventTypeInOrganization().addAll(eventTypes);
    }

    public static void deleteUserFrom(Organization organization, UserData user){
        organization.getUserList().remove(user);
    }
}
