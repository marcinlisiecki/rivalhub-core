package com.rivalhub.event.running;

import com.rivalhub.common.exception.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RunningResultSaver {
    private final RunningEventRepository runningEventRepository;
    private final RunningResultsMapper runningResultsMapper;
    private final UserTimeRepository userTimeRepository;
    private final RunningEventMapper runningEventMapper;

    public RunningEventViewDto save(Long eventId, List<UserTimesAddDto> userTimesAddDtos) {

        RunningEvent runningEvent = runningEventRepository
                .findById(eventId)
                .orElseThrow(EventNotFoundException::new);
        List<UserTime> userTimeList = new ArrayList<>();
        userTimesAddDtos.forEach(userTimesAddDto ->  userTimeList.add(runningResultsMapper.map(userTimesAddDto,runningEvent)));
        userTimeRepository.saveAll(userTimeList);
        return runningEventMapper.map(runningEventRepository.save(runningEvent));
    }

}
