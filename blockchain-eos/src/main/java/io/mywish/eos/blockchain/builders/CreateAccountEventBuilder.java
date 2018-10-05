package io.mywish.eos.blockchain.builders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.events.model.contract.CreateAccountEvent;
import io.mywish.eos.blockchain.model.EosActionAccountDefinition;
import org.springframework.stereotype.Component;

@Component
public class CreateAccountEventBuilder extends ActionEventBuilder<CreateAccountEvent> {
    private final static EosActionAccountDefinition DEFINITION = new EosActionAccountDefinition(
            "newaccount",
            "eosio"
    );

    public CreateAccountEventBuilder() {
    }

    @Override
    public CreateAccountEvent build(String address, ObjectNode objectNode) {
        return new CreateAccountEvent(
                DEFINITION,
                address,
                objectNode.get("creator").textValue(),
                objectNode.get("name").textValue()
        );
    }

    @Override
    public EosActionAccountDefinition getDefinition() {
        return DEFINITION;
    }
}