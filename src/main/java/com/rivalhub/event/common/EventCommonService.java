package com.rivalhub.event.common;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.exception.*;
import com.rivalhub.event.Event;
import com.rivalhub.event.EventDto;
import com.rivalhub.event.billiards.BilliardsEvent;
import com.rivalhub.event.match.Match;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.event.EventDto;
import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import com.rivalhub.user.profile.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCommonService {

    private final AutoMapper autoMapper;

    public <T extends Event> List<UserDetailsDto> findAllParticipants(CrudRepository<T, Long> repository, long id) {
        return repository
                .findById(id)
                .orElseThrow(EventNotFoundException::new)
                .getParticipants()
                .stream()
                .map(autoMapper::mapToUserDetails)
                .toList();
    }

    public <T extends Event> void setStatusForEvent(T event, EventDto eventDto) {
        LocalDateTime now = LocalDateTime.now();

        if (event.getEndTime().isAfter(now) && event.getStartTime().isAfter(now)) {
            eventDto.setStatus("Incoming");
            return;
        }
        if (event.getStartTime().isBefore(now) && event.getEndTime().isBefore(now)) {
            eventDto.setStatus("Historical");
            return;
        }
        eventDto.setStatus("Active");
    }

    public <T extends Event> void joinPublicEvent(CrudRepository<T, Long> repository, long id) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        var event = repository.findById(id).orElseThrow(EventNotFoundException::new);

        if (event.getParticipants().contains(requestUser)) throw new AlreadyEventParticipantException();

        if (event.isEventPublic()) {
            event.getParticipants().add(requestUser);
            repository.save(event);
            return;
        }
        throw new EventIsNotPublicException();
    }

    public <T extends Event> List<UserDetailsDto> deleteUserFromEvent(CrudRepository<T, Long> repository, long eventId, long userId) {
        T event = repository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        UserData user = event.getParticipants()
                .stream()
                .filter(userData -> userData.getId() == userId)
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
        if (event.getHost() == user)
            throw new HostRemoveException();
        event.getParticipants().remove(user);
        repository.save(event);
        return event.getParticipants().stream().map(UserMapper::map).toList();
    }
}
