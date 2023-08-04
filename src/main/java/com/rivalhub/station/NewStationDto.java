package com.rivalhub.station;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rivalhub.common.ErrorMessages;
import com.rivalhub.event.EventType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewStationDto {
    @JsonIgnore
    private Long id;
    private EventType type;
    private String name;

    @NotNull(message = ErrorMessages.ORGANIZATION_ID_IS_REQUIRED)
    private Long organizationId;
}
