package io.lastwill.eventscan.messages.swaps2;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class CancelledNotify extends Swaps2Notify {
    public CancelledNotify(String txHash, boolean isSuccess, String address, String id) {
        super(txHash, isSuccess, address, id);
    }

    @Override
    public String getType() {
        return "cancelled";
    }
}
