package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.domainDTO.TransactionHistoryDTO;
import com.jayrush.springmvcrest.domain.domainDTO.TransactionListDTO;
import com.jayrush.springmvcrest.utility.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

@Service
@Configuration
public class TransactionInterfaceImpl implements TransactionInterface {

    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionInterfaceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionInterfaceImpl() {

    }

    @Override
    public TerminalTransactions saveTransactions(TerminalTransactions terminalTransactions) {
        Date date = new Date();
        terminalTransactions.setDateCreated(date.toString());
        return transactionRepository.save(terminalTransactions);
    }

    @Override
    public List<TerminalTransactions> fetchTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public List<TerminalTransactions>getTransactionsByTID(String terminalID) {
        return transactionRepository.findByterminalID(terminalID);
    }

    @Override
    public Page<TerminalTransactions> fetchPaginatedTransactions(TerminalTransactions terminalTransactions, Pageable pageable) {
        List<TerminalTransactions> transactions = fetchTransactions();
        return new PageImpl<>(transactions,pageable,transactions.size());
    }

    @Override
    public TransactionListDTO getTransactionHistory(TransactionHistoryDTO transactionHistoryReq) {
        TransactionListDTO transactionListDTO = new TransactionListDTO();
        List<TerminalTransactions> historyRespDTOS;
        Page<TerminalTransactions> pagedTransactions;
        Pageable paged;

        if (transactionHistoryReq.getSize()>0 && transactionHistoryReq.getPage()>=0){
            paged = PageRequest.of(transactionHistoryReq.getPage(),transactionHistoryReq.getSize());
        }
        else {
            paged = PageRequest.of(0,1000000);
        }


        if(transactionHistoryReq.getFromDate()!=null && !transactionHistoryReq.getFromDate().equals("")
                && transactionHistoryReq.getToDate()!=null && !transactionHistoryReq.getToDate().equals("")
                && transactionHistoryReq.getInstitutionID()!=null && !transactionHistoryReq.getInstitutionID().equals("")){

            Date fromDate = DateUtil.dateFullFormat(transactionHistoryReq.getFromDate());
            Date toDate = DateUtil.dateFullFormat(transactionHistoryReq.getToDate());
            toDate = DateUtils.addDays(toDate,1);
            pagedTransactions = transactionRepository.findByinstitutionIDAndDateCreatedBetween(transactionHistoryReq.getInstitutionID(),fromDate,toDate,paged);
        }
        else {
            pagedTransactions = transactionRepository.findByinstitutionIDIgnoreCaseOrderByTime(transactionHistoryReq.getInstitutionID(),paged);
        }
        historyRespDTOS = pagedTransactions.getContent();
        if (pagedTransactions!=null && pagedTransactions.getContent().size()>0){
            transactionListDTO.setHasNextRecord(pagedTransactions.hasNext());
            transactionListDTO.setTotalCount((int) pagedTransactions.getTotalElements());
        }
        transactionListDTO.setTransactions(historyRespDTOS);
        return transactionListDTO;
    }
}
