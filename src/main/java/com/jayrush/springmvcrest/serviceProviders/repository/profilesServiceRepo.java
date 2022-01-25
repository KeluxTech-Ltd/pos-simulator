package com.jayrush.springmvcrest.serviceProviders.repository;

import com.jayrush.springmvcrest.serviceProviders.Models.profiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author JoshuaO
 */
@Repository
public interface profilesServiceRepo extends JpaRepository<profiles,Long> {
    profiles findByProfileName(String profileName);
}
