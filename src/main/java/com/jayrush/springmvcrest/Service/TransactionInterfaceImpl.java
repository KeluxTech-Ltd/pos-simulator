package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.domain.*;
import com.jayrush.springmvcrest.domain.domainDTO.TransactionHistoryDTO;
import com.jayrush.springmvcrest.domain.domainDTO.TransactionListDTO;
import com.jayrush.springmvcrest.domain.domainDTO.dateRange;
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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class TransactionInterfaceImpl implements TransactionInterface {
    private static final Logger logger = LoggerFactory.getLogger(TransactionInterfaceImpl.class);

    @Autowired
    TransactionInterface transactionInterface;
    @Autowired
    InstitutionRepository institutionRepository;
    @Autowired
    TerminalRepository terminalRepository;

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
//        List<TerminalTransactions> total = fetchTransactions();
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
            String from = fromDate.toString();
            String to = toDate.toString();
            pagedTransactions = transactionRepository.findTopByinstitutionIDAndDateTimeBetween(transactionHistoryReq.getInstitutionID(),from,to,paged);
        }
        else {
            pagedTransactions = transactionRepository.findTopByinstitutionID(transactionHistoryReq.getInstitutionID(),paged);
        }
        historyRespDTOS = pagedTransactions.getContent();
        if (pagedTransactions!=null && pagedTransactions.getContent().size()>0){
            transactionListDTO.setHasNextRecord(pagedTransactions.hasNext());
            transactionListDTO.setTotalCount((int) pagedTransactions.getTotalElements());
        }
        transactionListDTO.setTransactions(historyRespDTOS);
        return transactionListDTO;
    }

    @Override
    public TerminalTransactions getTransactionByID(Long id) {
        return transactionRepository.findById(id).get();
    }

    @Override
    public List<topFiveInstitutionDTO> getTopFiveInstitutions() {
        List<topFiveInstitutionDTO> topFiveInstitutionDTOS = new ArrayList<>();
//        List<List<String>> listListn  = transactionRepository.findTopfiveInstitution();
//
//        for (int i=0; i<listListn.size();i++){
//            topFiveInstitutionDTO dto = new topFiveInstitutionDTO();
//            dto.setInstitutionID(listListn.get(i));
//            topFiveInstitutionDTOS.add(i,dto);
//        }
        return topFiveInstitutionDTOS;
    }

    @Override
    public int getTotalInstitutions() {
        List<Institution> institutionList = institutionRepository.findAll();
        int totalInstitutions = institutionList.size();
        return totalInstitutions;
    }

    @Override
    public TransactionStatistics transactionStats() {
        TransactionStatistics tranStats = new TransactionStatistics();
        List<TerminalTransactions>transactions = transactionRepository.findByStatus("Success");
        List<TerminalTransactions>trancount = transactionRepository.findAll();
        BigDecimal amount = new BigDecimal(0);
        BigDecimal amount2 = new BigDecimal(0);
        BigDecimal totalAmount = new BigDecimal(0);

        for(int i = 0; i<transactions.size();i++){
            String value = transactions.get(i).getAmount();
            amount = new BigDecimal(value);


            totalAmount = totalAmount.add(amount2.add(amount));
        }



        tranStats.setSuccess(transactionRepository.findByStatus("Success").size());
        tranStats.setSuccess(transactionRepository.findByStatus("Failed").size());
        tranStats.setTotalSuccessfulAmount(totalAmount);
        tranStats.setTotalTransactions(trancount.size());

        return tranStats;
    }

    @Override
    public int terminalCount() {
        return terminalRepository.findAll().size();
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(yesterday());
    }
    @Override
    public List<List<String>> activeInactiveTerminals() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String today = simpleDateFormat.format(new Date());

        String yesterday = getYesterdayDateString();
        return transactionRepository.findActiveTerminals(yesterday,today);
    }

    @Override
    public List<TerminalTransactions> repushTransactions(dateRange dateRange) {
            List<TerminalTransactions> transactions = transactionRepository.findByInstitutionIDAndDateCreatedBetween(dateRange.getInstitutionID(), dateRange.getFrom(), dateRange.getTo());
            for (int i = 0; i<transactions.size();i++){
                transactions.get(i).setProcessed(false);
                transactionRepository.save(transactions.get(i));
            }
            return transactions;

    }
}
