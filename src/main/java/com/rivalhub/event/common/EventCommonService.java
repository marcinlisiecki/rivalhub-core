package com.rivalhub.event.common;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.event.Event;
import com.rivalhub.event.EventDto;
import com.rivalhub.user.UserDetailsDto;
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

    public <T extends Event> void setStatusForEvent(T event, EventDto eventDto){
        if (event.getEndTime().isAfter(LocalDateTime.now())
                &&
                event.getStartTime().isAfter(LocalDateTime.now())
        ) eventDto.setStatus("Incoming");
        else if (event.getStartTime().isBefore(LocalDateTime.now())
                &&
                event.getEndTime().isBefore(LocalDateTime.now())) eventDto.setStatus("Historical");
        else eventDto.setStatus("Active");
    }
}
