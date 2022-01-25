package com.jayrush.springmvcrest.Repositories;

import com.jayrush.springmvcrest.domain.terminalKeyManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author JoshuaO
 */
@Repository
public interface terminalKeysRepo extends JpaRepository<terminalKeyManagement, Long> {
    terminalKeyManagement findByTerminalID(String terminalID);
    List<terminalKeyManagement> findByParameterDownloadedOrMasterKeyOrPinKeyOrSessionKey(String param, String param2, String param3, String param4);
}
