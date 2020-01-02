package com.jayrush.springmvcrest.Repositories;

import com.jayrush.springmvcrest.domain.terminalKeyManagement;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author JoshuaO
 */
public interface terminalKeysRepo extends JpaRepository<terminalKeyManagement, Long> {
    terminalKeyManagement findByTerminalID(String terminalID);
}
