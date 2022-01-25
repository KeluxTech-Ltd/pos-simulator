package com.jayrush.springmvcrest.commission.service;

import com.jayrush.springmvcrest.commission.model.commission;
import com.jayrush.springmvcrest.commission.model.dtos.CommissionListDTO;
import com.jayrush.springmvcrest.commission.model.dtos.CommissionsHistoryDTO;
import com.jayrush.springmvcrest.commission.repository.commissionRepository;
import com.jayrush.springmvcrest.utility.DateUtil;
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
public class commissionServiceImpl implements commissionService {
    @Autowired
    commissionRepository commissionRepository;

    @Override
    public CommissionListDTO getCommissionHistory(CommissionsHistoryDTO commissionsHistoryDTO) {
        CommissionListDTO commissionListDTO = new CommissionListDTO();
        List<commission> historyRespDTOS;
//        List<TerminalTransactions> total = fetchTransactions();
        Page<commission> pagedTransactions;
        Pageable paged;

        if (commissionsHistoryDTO.getSize()>0 && commissionsHistoryDTO.getPage()>=0){
            paged = PageRequest.of(commissionsHistoryDTO.getPage(),commissionsHistoryDTO.getSize());
        }
        else {
            paged = PageRequest.of(0,1000000);
        }
        if (commissionsHistoryDTO.getInstitutionID()==null || commissionsHistoryDTO.getInstitutionID().equals("")){
            pagedTransactions = commissionRepository.SelectAll(paged);
            commissionListDTO.setTotalCount((int) pagedTransactions.getTotalElements());
        }


        else if(commissionsHistoryDTO.getFromDate()!=null && !commissionsHistoryDTO.getFromDate().equals("")
                && commissionsHistoryDTO.getToDate()!=null && !commissionsHistoryDTO.getToDate().equals("")
                && commissionsHistoryDTO.getInstitutionID()!=null && !commissionsHistoryDTO.getInstitutionID().equals("")){

            Date fromDate = DateUtil.dateFullFormat(commissionsHistoryDTO.getFromDate());
            Date toDate = DateUtil.dateFullFormat(commissionsHistoryDTO.getToDate());
            toDate = DateUtils.addDays(toDate,1);
            String from = fromDate.toString();
            String to = toDate.toString();
            pagedTransactions = commissionRepository.findTopByInstitutionIDAndDateBetween(commissionsHistoryDTO.getInstitutionID(),from,to,paged);
        }
        else {
            pagedTransactions = commissionRepository.findTopByInstitutionID(commissionsHistoryDTO.getInstitutionID(),paged);
        }
        historyRespDTOS = pagedTransactions.getContent();
        if (pagedTransactions!=null && pagedTransactions.getContent().size()>0){
            commissionListDTO.setHasNextRecord(pagedTransactions.hasNext());
            commissionListDTO.setTotalCount((int) pagedTransactions.getTotalElements());
        }
        commissionListDTO.setCommissionTransactions(historyRespDTOS);
        return commissionListDTO;
    }
}
