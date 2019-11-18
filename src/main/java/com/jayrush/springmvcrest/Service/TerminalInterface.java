package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.Terminals;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TerminalInterface {
    List<Terminals> getAllTerminals();
    Terminals getTerminalByID(Long id);
    Terminals RegisterTerminal(Terminals terminals);
    Terminals EditTerminal(Terminals terminals);
    Response uploadTerminals(MultipartFile file);
}
