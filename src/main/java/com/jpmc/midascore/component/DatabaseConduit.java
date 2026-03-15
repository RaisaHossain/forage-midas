package com.jpmc.midascore.component;

import com.jpmc.midascore.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void processTransaction(Transaction transaction) {
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()){
            String url = "http://localhost:8080/incentive";
            Incentive incentiveResponse = restTemplate.postForObject(url,transaction, Incentive.class);
            float incentiveAmount = incentiveResponse != null ? incentiveResponse.getAmount() : 0;
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);
            TransactionRecord record = new TransactionRecord();
            record.setSender(sender);
            record.setRecipient(recipient);

            userRepository.save(sender);
            userRepository.save(recipient);
            transactionRepository.save(record);
        }
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

}
