package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    private final DatabaseConduit transactionService;

    public TransactionConsumer(DatabaseConduit transactionService){
        this.transactionService = transactionService;
    }

    @KafkaListener(topics="${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        transactionService.processTransaction(transaction);
    }
}
