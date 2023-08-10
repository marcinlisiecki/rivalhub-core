package com.rivalhub.event;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Setter;
import java.io.Serializable;

public enum EventType {
    PING_PONG,
    BILARD,
    PILKARZYKI,
    RZUTKI,
    PODCIAGANIE;
}
