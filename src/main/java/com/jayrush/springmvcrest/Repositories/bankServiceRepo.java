package com.jayrush.springmvcrest.Repositories;

import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface bankServiceRepo extends JpaRepository<bank, Long> {
    bank findByCbnCode(String bankcode);
}
