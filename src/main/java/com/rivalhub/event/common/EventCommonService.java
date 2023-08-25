package com.rivalhub.event.common;

import com.rivalhub.common.AutoMapper;
import com.rivalhub.common.exception.EventNotFoundException;
import com.rivalhub.event.Event;
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
}
