package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;


    public TerminalTransactions saveTransactionstoDb(TerminalTransactions terminalTransactions) {
        Date date = new Date();
        terminalTransactions.setDateCreated(date.toString());

        return transactionRepository.save(terminalTransactions);

    }
}
