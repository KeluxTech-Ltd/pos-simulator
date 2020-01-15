package com.jayrush.springmvcrest.Repositories;

//import com.jayrush.springmvcrest.domain.globalSettings;
import com.jayrush.springmvcrest.domain.globalSettings;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author JoshuaO
 */
public interface globalSettingsRepo extends JpaRepository<globalSettings, Long> {
}
