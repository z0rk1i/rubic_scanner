package io.mywish.blockchain.service.transaction.receipt;

import io.mywish.eoscli4j.model.TransactionStatus;
import io.mywish.blockchain.ContractEvent;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.WrapperTransactionReceipt;
import io.mywish.blockchain.model.output.WrapperOutputEos;
import io.mywish.blockchain.service.log.WrapperLogEosService;
import io.mywish.blockchain.transaction.WrapperTransactionEos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WrapperTransactionReceiptEosService {
    @Autowired
    private WrapperLogEosService logBuilder;

    public WrapperTransactionReceipt build(WrapperTransaction wrapperTransaction) {
        WrapperTransactionEos transaction = (WrapperTransactionEos) wrapperTransaction;
        List<String> contracts = Collections.emptyList();
        List<ContractEvent> logs = transaction
                .getOutputs()
                .stream()
                .filter(wrapperOutput -> wrapperOutput instanceof WrapperOutputEos)
                .map(wrapperOutput -> (WrapperOutputEos) wrapperOutput)
                .map(logBuilder::build)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Boolean success = transaction.getStatus() == TransactionStatus.Executed;

        return new WrapperTransactionReceipt(
                wrapperTransaction.getHash(),
                contracts,
                logs,
                success
        );
    }
}
