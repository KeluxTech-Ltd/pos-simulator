package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.domainDTO.TransactionHistoryDTO;
import com.jayrush.springmvcrest.domain.domainDTO.TransactionListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface TransactionInterface {
    TerminalTransactions saveTransactions(TerminalTransactions terminalTransactions);
    List<TerminalTransactions> fetchTransactions();
    List<TerminalTransactions> getTransactionsByTID(String terminalID);
    Page<TerminalTransactions> fetchPaginatedTransactions(TerminalTransactions terminalTransactions, Pageable pageable);

    TransactionListDTO getTransactionHistory(TransactionHistoryDTO transactionHistory);
}
