package com.jayrush.springmvcrest.Repositories;

import com.jayrush.springmvcrest.domain.Terminals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TerminalRepository extends JpaRepository<Terminals, Long> {
    Terminals findByterminalID(String terminalID);
    List <Terminals> findAllByDateCreatedOrderByDateCreated(String terminalID);
}

