package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.Swaps2Order;
import io.lastwill.eventscan.model.User;
import lombok.Getter;

@Getter
public class Swaps2NotificationMQEvent extends BaseEvent {
    private final Swaps2Order order;
    private final User user;


    public Swaps2NotificationMQEvent(Swaps2Order order, User user) {
        super(NetworkType.ETHEREUM_MAINNET);
        this.order = order;
        this.user = user;
    }
}
