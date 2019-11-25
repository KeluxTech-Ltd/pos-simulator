package com.jayrush.springmvcrest.Repositories;

import com.jayrush.springmvcrest.domain.TerminalTransactions;
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

    List<TerminalTransactions> findByinstitutionIDIgnoreCaseOrderByDate(String institutionID);

    Page<TerminalTransactions>findByinstitutionIDAndDateCreatedBetween(String institutionID, Date from, Date to, Pageable pageable);

    Page<TerminalTransactions> findByinstitutionID(String institutionID, Pageable paged);

    @Query(value = "select t from TerminalTransactions t  order by t.dateCreated desc")
    Page<TerminalTransactions> SelectAll(Pageable pageable);
}
