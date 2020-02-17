package com.jayrush.springmvcrest.commission.repository;

import com.jayrush.springmvcrest.commission.model.commission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author JoshuaO
 */
@Repository
public interface commissionRepository extends JpaRepository<commission,Long> {
    @Query(value = "select t from commission t  order by t.id desc")
    Page<commission> SelectAll(Pageable pageable);
    Page<commission> findTopByInstitutionIDAndDateBetween(String institutionID, String from, String to, Pageable pageable);

    Page<commission> findTopByInstitutionID(String institutionID, Pageable paged);

}
