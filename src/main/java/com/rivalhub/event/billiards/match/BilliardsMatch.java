package com.rivalhub.event.billiards.match;

import com.rivalhub.event.match.Match;
import com.rivalhub.user.UserData;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class BilliardsMatch extends Match {


    private boolean team1PlaysFull;
    private boolean team1HadPottedFirst;
    private WinType winType;
    private int howManyBillsLeftTeam1;
    private int howManyBillsLeftTeam2;
    private boolean team1Won;
    private boolean team2Won;

}
