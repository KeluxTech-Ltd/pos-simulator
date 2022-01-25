package com.jayrush.springmvcrest.serviceProviders.repository;

import com.jayrush.springmvcrest.serviceProviders.Models.serviceProviders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author JoshuaO
 */
@Repository
public interface serviceProviderRepo extends JpaRepository<serviceProviders,Long> {
    serviceProviders findByProviderName(String providerName);
}
