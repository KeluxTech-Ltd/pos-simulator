package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.domainDTO.TransactionHistoryDTO;
import com.jayrush.springmvcrest.domain.domainDTO.TransactionListDTO;
import com.jayrush.springmvcrest.utility.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TransactionInterfaceImpl implements TransactionInterface {
    private static final Logger logger = LoggerFactory.getLogger(TransactionInterfaceImpl.class);

    @Autowired
    TransactionInterface transactionInterface;


    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionInterfaceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionInterfaceImpl() {

    }

    @Override
    public TerminalTransactions saveTransactions(TerminalTransactions terminalTransactions) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = simpleDateFormat.format(new Date());

        terminalTransactions.setDateCreated(date);
        return transactionRepository.save(terminalTransactions);
    }

    @Override
    public List<TerminalTransactions> fetchTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public List<TerminalTransactions> getAllUnnotifiedTransactions(boolean processed,boolean tranComplete) {
        return transactionRepository.findByProcessedAndTranComplete(processed,tranComplete);
    }

    @Override
    public List<TerminalTransactions>getTransactionsByinstitutionID(String institutionID) {
        return transactionRepository.findByinstitutionIDIgnoreCaseOrderByDate(institutionID);
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
        List<TerminalTransactions> total = fetchTransactions();
        Page<TerminalTransactions> pagedTransactions;
        Pageable paged;

        if (transactionHistoryReq.getSize()>0 && transactionHistoryReq.getPage()>=0){
            paged = PageRequest.of(transactionHistoryReq.getPage(),transactionHistoryReq.getSize());
        }
        else {
            paged = PageRequest.of(0,1000000);
        }
        if (transactionHistoryReq.getInstitutionID()==null || transactionHistoryReq.getInstitutionID().equals("")){
            pagedTransactions = transactionRepository.SelectAll(paged);
            transactionListDTO.setTotalCount((int) pagedTransactions.getTotalElements());
        }


        else if(transactionHistoryReq.getFromDate()!=null && !transactionHistoryReq.getFromDate().equals("")
                && transactionHistoryReq.getToDate()!=null && !transactionHistoryReq.getToDate().equals("")
                && transactionHistoryReq.getInstitutionID()!=null && !transactionHistoryReq.getInstitutionID().equals("")){

            Date fromDate = DateUtil.dateFullFormat(transactionHistoryReq.getFromDate());
            Date toDate = DateUtil.dateFullFormat(transactionHistoryReq.getToDate());
            toDate = DateUtils.addDays(toDate,1);
            pagedTransactions = transactionRepository.findByinstitutionIDAndDateCreatedBetween(transactionHistoryReq.getInstitutionID(),fromDate,toDate,paged);
        }
        else {
            pagedTransactions = transactionRepository.findByinstitutionID(transactionHistoryReq.getInstitutionID(),paged);
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
