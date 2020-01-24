package com.jayrush.springmvcrest.Repositories;

import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.topFiveInstitutionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TerminalTransactions, Long> {

    TerminalTransactions findByrrnAndTerminalID(String rrn, String tid);

    TerminalTransactions findByrrnAndId(String rrn, Long id);

    List<TerminalTransactions> findByinstitutionIDIgnoreCaseOrderByDate(String institutionID);

    List<TerminalTransactions> findByStatus(String status);

    List<TerminalTransactions> findByProcessedAndTranComplete(boolean processed, boolean tranComplete);

    Page<TerminalTransactions>findByinstitutionIDAndDateCreatedBetween(String institutionID, Date from, Date to, Pageable pageable);

    Page<TerminalTransactions> findByinstitutionID(String institutionID, Pageable paged);

    @Query(value = "select t from TerminalTransactions t  order by t.dateCreated desc")
    Page<TerminalTransactions> SelectAll(Pageable pageable);

    @Query(value = "select * from transaction_logs t\n" +
            "where t.response_code = '00'\n" +
            "group by t.institutionid\n" +
            "order by count(0) desc\n" +
            "limit 5",nativeQuery = true)
    List<TerminalTransactions> findTopfiveInstitution();


    @Query(value = "select count(distinct  terminalid) from dbo.transaction_logs \n" +
            "where date_created between ?1 and  ?2",nativeQuery = true)
    List<List<String>> findActiveTerminals(String from, String to);

    List<TerminalTransactions> findByInstitutionIDAndDateCreatedBetween(String institutionID, String from, String to);

//    @Query(value = "SELECT *\n" +
//            "FROM transaction_logs\n" +
//            "ORDER BY id DESC \n" +
//            "limit 10", nativeQuery = true)
    @Query(value = "SELECT TOP 10 t.* FROM dbo.transaction_logs t", nativeQuery = true)
    List<TerminalTransactions> getRecentTransactions();

    //total successful monthly traansactions(Month)
//    @Query(value = "SELECT * FROM transaction_logs WHERE date_created <= (NOW() - INTERVAL 1 MONTH)\n" +
//            "and status = 'Success'", nativeQuery = true)
//    List<TerminalTransactions> getSuccessfulTransactions();
    List<TerminalTransactions>findByStatusAndDateCreatedBetween(String status, String from, String to);

    //total failed transactions(1 month)
//    @Query(value = "SELECT * FROM transaction_logs WHERE date_created <= (NOW() - INTERVAL 1 MONTH)\n" +
//            "and status = 'Failed'", nativeQuery = true)
//    List<TerminalTransactions> getFailedTransactions();
//
//    @Query(value = "Select sum(transaction_logs.amount) \n" +
//            "from transaction_logs\n" +
//            "where status = 'success'\n" +
//            "and date_created <=(NOW() - INTERVAL 1 MONTH)", nativeQuery = true)
    @Query(value = "select sum(CAST(amount AS FLOAT)) from transaction_logs where status = 'success'\n" +
            "and date_created between ?1 and ?2", nativeQuery = true)
    Double transactionAmount(String from, String to);






}
