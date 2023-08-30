package com.rivalhub.event.pullups.match.result;

import com.rivalhub.user.UserData;
import com.rivalhub.user.UserDetailsDto;
import jakarta.persistence.ManyToOne;
import lombok.Data;


import java.util.List;

@Data
public class PullUpSeriesDto {
    private Long userId;
    private Long score;
    private Long seriesID;

}
