package com.rivalhub.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventType {

    PING_PONG("PING_PONG");

    private String type;
}
