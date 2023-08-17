package com.rivalhub.reservation;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddReservationDTO {
    private Long organizationId;
    private List<Long> stationsIdList;
    private String startTime;
    private String endTime;
}
