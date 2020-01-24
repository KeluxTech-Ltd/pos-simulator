package com.jayrush.springmvcrest.Repositories;

//import com.jayrush.springmvcrest.domain.globalSettings;
import com.jayrush.springmvcrest.domain.globalSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author JoshuaO
 */
@Repository
public interface globalSettingsRepo extends JpaRepository<globalSettings, Long> {
}
