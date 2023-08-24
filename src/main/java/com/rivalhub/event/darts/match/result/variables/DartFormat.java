package com.rivalhub.event.darts.match.result.variables;

import lombok.Data;


public enum DartFormat {
    _301(301),
    _501(501),
    _701(701),
    _901(901);

    public final int maxScore;
    DartFormat(int maxScore) {
        this.maxScore = maxScore;
    }
}
