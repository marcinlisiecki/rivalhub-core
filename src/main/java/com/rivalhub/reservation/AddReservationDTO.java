package com.rivalhub.reservation;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class AddReservationDTO {
    //zakładam że fron powinien wiedzieć wjakiej organizacji się znajdujemy i nam to przekazać
    private Long organizationId;
    private List<Long> stationsIdList;
    private String startTime;
    private String endTime;
}
