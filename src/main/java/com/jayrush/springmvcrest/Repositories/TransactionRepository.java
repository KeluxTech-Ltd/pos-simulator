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
    TerminalTransactions findByrrn(String rrn);

    TerminalTransactions findByrrnAndStanAndTerminalIDAndAmount(String rrn, String stan, String tid, String amount);

    TerminalTransactions findByrrnAndTerminalID(String rrn, String tid);

    TerminalTransactions findByrrnAndId(String rrn, Long id);

    List<TerminalTransactions> findByinstitutionIDIgnoreCaseOrderByDate(String institutionID);

    List<TerminalTransactions> findByStatus(String status);

    List<TerminalTransactions> findByProcessedAndTranComplete(boolean processed, boolean tranComplete);

    Page<TerminalTransactions>findByinstitutionIDAndDateCreatedBetween(String institutionID, Date from, Date to, Pageable pageable);

    Page<TerminalTransactions> findByinstitutionID(String institutionID, Pageable paged);

    @Query(value = "select t from TerminalTransactions t  order by t.dateCreated desc")
    Page<TerminalTransactions> SelectAll(Pageable pageable);

    @Query(value = "select count(*) as cnt ,t.institutionid from transaction_logs t\n" +
            "where t.response_code = '00'\n" +
            "group by t.institutionid\n" +
            "order by count(0) desc\n" +
            "limit 5",nativeQuery = true)
    List<List<String>> findTopfiveInstitution();


    @Query(value = "select count(distinct  terminalid) from transaction_logs \n" +
            "where date_created between ?1 and  ?2",nativeQuery = true)
    List<List<String>> findActiveTerminals(String from, String to);

    List<TerminalTransactions> findByInstitutionIDAndDateCreatedBetween(String institutionID, String from, String to);



}
