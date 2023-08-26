package com.rivalhub.event.common;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.exception.AlreadyEventParticipantException;
import com.rivalhub.common.exception.EventIsNotPublicException;
import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.event.Event;
import com.rivalhub.event.EventDto;
import com.rivalhub.security.SecurityUtils;
import com.rivalhub.user.UserDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

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

    public <T extends Event> void joinPublicEvent(CrudRepository<T, Long> repository, long id) {
        var requestUser = SecurityUtils.getUserFromSecurityContext();
        var event = repository.findById(id).orElseThrow(EventNotFoundException::new);

        if(event.getParticipants().contains(requestUser)) throw new AlreadyEventParticipantException();

        if (event.isEventPublic()) {
            event.getParticipants().add(requestUser);
            repository.save(event);
            return;
        }
        throw new EventIsNotPublicException();
    }
}
