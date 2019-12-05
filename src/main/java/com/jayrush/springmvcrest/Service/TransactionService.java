package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;


    public TerminalTransactions saveTransactionstoDb(TerminalTransactions terminalTransactions) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        terminalTransactions.setDateCreated(date);

        return transactionRepository.save(terminalTransactions);

    }
}
