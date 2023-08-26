package com.rivalhub.event.match;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MatchDto {
    private Long id;
    private List<Long> team1Ids;
    private List<Long> team2Ids;
    private Map<Long, Boolean> userApprovalMap = new HashMap<>();
    private String dartFormat;
    private String dartMode;
}
