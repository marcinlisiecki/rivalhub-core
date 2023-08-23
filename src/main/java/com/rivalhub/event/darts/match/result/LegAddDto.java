package com.rivalhub.event.darts.match.result;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LegAddDto {
    private List<DartRoundDto> roundList = new ArrayList<>();

}
