package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author JoshuaO
 */
@Service
public class dashboardInterfaceImpl implements dashboardInterface {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    TerminalRepository terminalRepository;
    @Autowired
    TransactionInterface transactionInterface;
    @Autowired
    InstitutionRepository institutionRepository;




    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
    private Date month() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30);
        return cal.getTime();
    }
    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(yesterday());
    }
    private String getlastmonthDateString() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(month());
    }
    private String getlastmonthDateString2() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(month());
    }

    @Override
    public DashboardUtils getDashboardUtils() {
        DashboardUtils dashboardUtils = new DashboardUtils();
        //terminal Count
        int terminalCount = terminalRepository.findAll().size();
        dashboardUtils.setTotalTerminals(terminalCount);

        //Institution count
        List<Institution> institutionList = institutionRepository.findAll();
        int institutionCount = institutionList.size();
        dashboardUtils.setTotalInstitutions(institutionCount);

        //Dashboard transactions
        List<TerminalTransactions> transactions = transactionRepository.getRecentTransactions();
        dashboardUtils.setDashboardTransactions(transactions);

        //active inactive
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String today = simpleDateFormat.format(new Date());

        String yesterday = getYesterdayDateString();
        List<List<String>> activeTerminals = transactionRepository.findActiveTerminals(yesterday,today);
        activeInactive activeInactive = new activeInactive();
        activeInactive.setActiveTerminals(activeTerminals.get(0).get(0));


        int totalActive = Integer.parseInt(activeInactive.getActiveTerminals());
        int totalInactive = terminalCount-totalActive;

        activeInactive.setActiveTerminals(String.valueOf(totalActive));
        activeInactive.setInactiveTerminals(String.valueOf(totalInactive));

        dashboardUtils.setActiveInactiveTerminals(activeInactive);


        //total Successful in the month
        String lastmonth = getlastmonthDateString();
        List<TerminalTransactions> successfulTransactions = transactionRepository.findByStatusAndDateCreatedBetween("Success",today,lastmonth);
        dashboardUtils.setSuccess(successfulTransactions.size());

        //failed transactions in the month
        List<TerminalTransactions> failedTransactions = transactionRepository.findByStatusAndDateCreatedBetween("Failed",today,lastmonth);
        dashboardUtils.setFailed(failedTransactions.size());

        //total transactions in the month
        dashboardUtils.setTotalTransactions(successfulTransactions.size()+failedTransactions.size());

        //successful Amount in the month
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String now = simpleDateFormat2.format(new Date());

        String lastmonthDate = getlastmonthDateString2();
        Double successfulAmount = transactionRepository.transactionAmount(lastmonth,now);
        dashboardUtils.setTotalSuccessfulAmount(successfulAmount);
        return dashboardUtils;
    }
}
