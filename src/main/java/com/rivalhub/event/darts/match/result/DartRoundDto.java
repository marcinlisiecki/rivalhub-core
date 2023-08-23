package com.rivalhub.event.darts.match.result;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DartRoundDto {
    List<SinglePlayerScoreInRoundDto> singlePlayerScoreInRoundsList = new ArrayList<>();
}
