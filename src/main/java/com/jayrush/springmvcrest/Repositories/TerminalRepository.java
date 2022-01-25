package com.jayrush.springmvcrest.Repositories;

import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.Terminals;
import com.jayrush.springmvcrest.serviceProviders.Models.profiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TerminalRepository extends JpaRepository<Terminals, Long> {
    Terminals findByterminalID(String terminalID);
    Terminals findByTerminalIDAndInstitution_InstitutionID(String terminalID, String institutionID);
    List <Terminals> findAllByDateCreatedOrderByDateCreated(String terminalID);
    Page<Terminals> findByInstitution_Id(Long institutionID, Pageable paged);
    List<Terminals>findByInstitution_InstitutionID(String institutionID);
}

