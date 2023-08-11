package com.rivalhub.organization.service;


import com.rivalhub.event.EventType;
import com.rivalhub.organization.Organization;
import com.rivalhub.station.Station;
import com.rivalhub.user.UserData;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class UserOrganizationService {
    public static void addUser(UserData userData, Organization organization){
        userData.getOrganizationList().add(organization);
        organization.getUserList().add(userData);
    }

    public static void addAdminUser(UserData userData, Organization organization){
        userData.getOrganizationList().add(organization);
        organization.getUserList().add(userData);
        organization.getAdminUsers().add(userData);
    }

    public static void addStation(Station station, Organization organization){
        organization.getStationList().add(station);
    }

    public static void removeStation(Station station, Organization organization){
        organization.getStationList().remove(station);
    }

    public static void addAllEventTypes(Organization organization) {
        Set<EventType> eventTypes = Arrays.stream(EventType.values()).collect(Collectors.toSet());
        organization.setEventTypeInOrganization(eventTypes);
    }

    public static void removeEventType(Organization organization, EventType eventType) {
        organization.getEventTypeInOrganization().remove(eventType);
    }

    public static void addEventType(Organization organization, EventType eventType) {
        organization.getEventTypeInOrganization().add(eventType);
    }
}
