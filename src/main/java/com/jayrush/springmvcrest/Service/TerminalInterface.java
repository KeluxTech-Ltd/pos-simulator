package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.Terminals;
import com.jayrush.springmvcrest.domain.domainDTO.PagedInstitutionRequestDTO;
import com.jayrush.springmvcrest.domain.domainDTO.PagedRequestDTO;
import com.jayrush.springmvcrest.domain.domainDTO.TerminalListDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TerminalInterface {
    List<Terminals> getAllTerminals();
    Terminals getTerminalByID(Long id);
    Terminals RegisterTerminal(Terminals terminals);
    Terminals EditTerminal(Terminals terminals);
    Response uploadTerminals(MultipartFile file);
    TerminalListDTO getPagenatedTerminals(PagedRequestDTO pagedTerminalsDTO);
    TerminalListDTO getPagenatedTerminalsByInstitution(PagedInstitutionRequestDTO institution);
}
