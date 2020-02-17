package com.jayrush.springmvcrest.wallet.service;

import com.jayrush.springmvcrest.utility.DateUtil;
import com.jayrush.springmvcrest.wallet.models.dtos.WalletTransactionHistoryDTO;
import com.jayrush.springmvcrest.wallet.models.dtos.WalletTransactionListDTO;
import com.jayrush.springmvcrest.wallet.models.walletTransaction;
import com.jayrush.springmvcrest.wallet.repository.walletTransactionRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author JoshuaO
 */
@Service
public class walletTransactionServicesImpl implements walletTransactionServices {
    @Autowired
    walletTransactionRepository walletTransactionRepository;


    @Override
    public WalletTransactionListDTO getWalletTransactionHistory(WalletTransactionHistoryDTO walletTransactionHistory) {
        WalletTransactionListDTO transactionListDTO = new WalletTransactionListDTO();
        List<walletTransaction> historyRespDTOS;
//        List<TerminalTransactions> total = fetchTransactions();
        Page<walletTransaction> pagedTransactions;
        Pageable paged;

        if (walletTransactionHistory.getSize()>0 && walletTransactionHistory.getPage()>=0){
            paged = PageRequest.of(walletTransactionHistory.getPage(),walletTransactionHistory.getSize());
        }
        else {
            paged = PageRequest.of(0,1000000);
        }
        if (walletTransactionHistory.getWalletNumber()==null || walletTransactionHistory.getWalletNumber().equals("")){
            pagedTransactions = walletTransactionRepository.SelectAll(paged);
            transactionListDTO.setTotalCount((int) pagedTransactions.getTotalElements());
        }


        else if(walletTransactionHistory.getFromDate()!=null && !walletTransactionHistory.getFromDate().equals("")
                && walletTransactionHistory.getToDate()!=null && !walletTransactionHistory.getToDate().equals("")
                && walletTransactionHistory.getWalletNumber()!=null && !walletTransactionHistory.getWalletNumber().equals("")){

            Date fromDate = DateUtil.dateFullFormat(walletTransactionHistory.getFromDate());
            Date toDate = DateUtil.dateFullFormat(walletTransactionHistory.getToDate());
            toDate = DateUtils.addDays(toDate,1);
            String from = fromDate.toString();
            String to = toDate.toString();
            pagedTransactions = walletTransactionRepository.findTopByWalletNumberAndTranDateBetween(walletTransactionHistory.getWalletNumber(),from,to,paged);
        }
        else {
            pagedTransactions = walletTransactionRepository.findTopByWalletNumber(walletTransactionHistory.getWalletNumber(),paged);
        }
        historyRespDTOS = pagedTransactions.getContent();
        if (pagedTransactions!=null && pagedTransactions.getContent().size()>0){
            transactionListDTO.setHasNextRecord(pagedTransactions.hasNext());
            transactionListDTO.setTotalCount((int) pagedTransactions.getTotalElements());
        }
        transactionListDTO.setWalletTransactions(historyRespDTOS);
        return transactionListDTO;
    }
}
