package io.lastwill.eventscan.events.model;

import io.lastwill.eventscan.events.model.contract.swaps2.RefundEvent;
import io.lastwill.eventscan.model.CryptoCurrency;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.model.Swaps2Order;
import io.lastwill.eventscan.model.User;
import io.mywish.blockchain.WrapperTransaction;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class SwapsOrderRefundEvent extends BaseEvent {

    private final String token;
    private final String userAddress;
    private final BigInteger amount;
    private final Swaps2Order order;
    private final WrapperTransaction transaction;
    private final User user;
    CryptoCurrency currency;

    public SwapsOrderRefundEvent(NetworkType networkType,
                                  Swaps2Order order,
                                  WrapperTransaction transaction,
                                  RefundEvent event,
                                  User user,
                                  CryptoCurrency cryptoCurrency
    ) {
        super(networkType);
        this.order = order;
        this.transaction = transaction;
        this.token = event.getToken();
        this.userAddress = event.getUser();
        this.amount = event.getAmount();
        this.user = user;
        this.currency = cryptoCurrency;
    }
}
