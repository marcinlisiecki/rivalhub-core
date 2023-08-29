package com.rivalhub.event.billiards.match;

import lombok.Data;

@Data
public class BilliardsMatchResultAdd {
    private boolean team1PlaysFull;
    private boolean team1HadPottedFirst;
    private WinType winType;
    private int howManyBillsLeftTeam1;
    private int howManyBillsLeftTeam2;
    private boolean team1Won;
    private boolean team2Won;
}
