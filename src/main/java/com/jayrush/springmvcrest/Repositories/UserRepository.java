package com.jayrush.springmvcrest.Repositories;

import com.jayrush.springmvcrest.domain.tmsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<tmsUser,Long > {
    tmsUser findByusername(String username);
}
