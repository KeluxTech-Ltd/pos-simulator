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

    TerminalTransactions findByTerminalIDAndRequestDateTimeAndTime(String terminalId, String requestDateTime,String time);

    List<TerminalTransactions> findByinstitutionIDIgnoreCaseOrderByDate(String institutionID);

    List<TerminalTransactions> findByStatus(String status);

    List<TerminalTransactions> findByProcessedAndTranComplete(boolean processed, boolean tranComplete);

    Page<TerminalTransactions>findByinstitutionIDAndDateTimeBetween(String institutionID, Date from, Date to, Pageable pageable);
    Page<TerminalTransactions>findTopByinstitutionIDAndDateTimeBetween(String institutionID, String from, String to, Pageable pageable);

    Page<TerminalTransactions> findTopByinstitutionID(String institutionID, Pageable paged);

    @Query(value = "select t from TerminalTransactions t  order by t.id desc")
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

    @Query(value = "select count(distinct  terminalid) from dbo.transaction_logs \n" +
            "where institutionid = ?1 and date_created between ?2 and  ?3",nativeQuery = true)
    List<List<String>> findActiveTerminalsbyInstitution(String institutionID, String from, String to);

    List<TerminalTransactions> findByInstitutionIDAndDateCreatedBetween(String institutionID, String from, String to);


    @Query(value = "SELECT TOP(10) * FROM transaction_logs ORDER BY id DESC", nativeQuery = true)
    List<TerminalTransactions> getRecentTransactions();

    @Query(value = "SELECT TOP(10) * FROM dbo.transaction_logs where institutionid = ?1 ORDER BY id DESC", nativeQuery = true)
    List<TerminalTransactions> getRecentInstitutionTransactions(String institutionID);


    List<TerminalTransactions>findByStatusAndDateCreatedBetween(String status, String from, String to);
    List<TerminalTransactions>findByInstitutionIDAndStatusAndDateCreatedBetween(String institutionID,String status, String from, String to);


    @Query(value = "select sum(CAST(amount AS FLOAT)) from transaction_logs where status = 'success'\n" +
            "and date_created between ?1 and ?2", nativeQuery = true)
    Double transactionAmount(String from, String to);

    @Query(value = "select sum(CAST(amount AS FLOAT)) from transaction_logs where institutionid = ?1 and status = 'success'\n" +
            "and date_created between ?2 and ?3", nativeQuery = true)
    Double institutiontransactionAmount(String institutionID,String from, String to);






}
