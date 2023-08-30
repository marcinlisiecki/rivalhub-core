package com.rivalhub.organization;

import com.rivalhub.user.UserDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsDTO {
    UserDetailsDto userDetailsDto;
    Long games;
    Long winGames;
}
