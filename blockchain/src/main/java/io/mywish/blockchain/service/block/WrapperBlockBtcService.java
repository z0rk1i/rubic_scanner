package io.mywish.blockchain.service.block;

import io.mywish.blockchain.WrapperBlock;
import io.mywish.blockchain.WrapperTransaction;
import io.mywish.blockchain.service.transaction.WrapperTransactionBtcService;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WrapperBlockBtcService {
    @Autowired
    private WrapperTransactionBtcService transactionBuilder;

    public WrapperBlock build(Block block, Long height, NetworkParameters networkParameters) {
        String hash = block.getHashAsString();
        Long timestamp = block.getTimeSeconds();
        List<WrapperTransaction> transactions = block
                .getTransactions()
                .stream()
                .map(tx -> transactionBuilder.build(tx, networkParameters))
                .collect(Collectors.toList());
        return new WrapperBlock(hash, height, timestamp, transactions);
    }
}
