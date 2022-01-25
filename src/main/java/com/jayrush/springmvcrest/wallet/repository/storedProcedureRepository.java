package com.jayrush.springmvcrest.wallet.repository;

import com.jayrush.springmvcrest.domain.domainDTO.storedProcedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.StoredProcedureQuery;

/**
 * @author JoshuaO
 */
@Repository
public interface storedProcedureRepository extends JpaRepository<storedProcedure,Long> {
    @Query(value = "{call fundWallet(?)}",nativeQuery = true)
    String startFundWallet(Long id);
}
