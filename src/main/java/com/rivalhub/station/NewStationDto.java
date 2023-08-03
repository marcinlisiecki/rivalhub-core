package com.rivalhub.station;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewStationDto {
    private Long id;
    private String type;
    private String name;
}
