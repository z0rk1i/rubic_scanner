package io.mywish.eos.blockchain.builders;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lastwill.eventscan.events.model.contract.InitializedEvent;
import io.mywish.blockchain.ContractEventDefinition;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component("EosFinalizedEventBuilder")
@NoArgsConstructor
public class FinalizedEventBuilder extends ActionEventBuilder<InitializedEvent> {
    private final static ContractEventDefinition DEFINITION = new ContractEventDefinition(
            "finalize"
    );

    @Override
    public InitializedEvent build(String address, ObjectNode data) {
        return new InitializedEvent(DEFINITION, address);
    }

    @Override
    public ContractEventDefinition getDefinition() {
        return DEFINITION;
    }
}
