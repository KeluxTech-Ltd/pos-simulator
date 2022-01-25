package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.domain.*;
import com.jayrush.springmvcrest.domain.domainDTO.TransactionHistoryDTO;
import com.jayrush.springmvcrest.domain.domainDTO.TransactionListDTO;
import com.jayrush.springmvcrest.domain.domainDTO.dateRange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface TransactionInterface {
    TerminalTransactions saveTransactions(TerminalTransactions terminalTransactions);
    List<TerminalTransactions> fetchTransactions();
    List<TerminalTransactions> getAllUnnotifiedTransactions(boolean processed,boolean tranComplete);
    List<TerminalTransactions> getTransactionsByinstitutionID(String terminalID);
    Page<TerminalTransactions> fetchPaginatedTransactions(TerminalTransactions terminalTransactions, Pageable pageable);
    TransactionListDTO getTransactionHistory(TransactionHistoryDTO transactionHistory);
    TerminalTransactions getTransactionByID(Long id);
    List<topFiveInstitutionDTO> getTopFiveInstitutions();
    int getTotalInstitutions();
    TransactionStatistics transactionStats();
    int terminalCount();
    List<List<String>> activeInactiveTerminals();
    List<TerminalTransactions> repushTransactions(dateRange dateRange);
    List<TerminalTransactions> getAllUnnotifiedTransactions2();

//    TerminalTransactions search(String terminalid, String rrn, String stan);
}
