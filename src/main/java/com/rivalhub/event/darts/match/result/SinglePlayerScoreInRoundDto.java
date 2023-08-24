package com.rivalhub.event.darts.match.result;

import com.rivalhub.user.UserData;
import lombok.Data;

@Data
public class SinglePlayerScoreInRoundDto {
    private Long score;
    private Long blanks;
}
