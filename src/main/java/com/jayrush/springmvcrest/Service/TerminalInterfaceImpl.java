package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.Terminals;
import com.jayrush.springmvcrest.domain.domainDTO.TerminalsDTO;
import io.github.mapper.excel.ExcelMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class TerminalInterfaceImpl implements TerminalInterface {
    private static Logger logger = LoggerFactory.getLogger(TerminalInterfaceImpl.class);
    @Value("${file-path}")
    private String path;


    @Autowired
    TerminalRepository terminalRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public List<Terminals> getAllTerminals() {
        return terminalRepository.findAll();
    }

    @Override
    public Terminals getTerminalByID(Long id) {
        return terminalRepository.findById(id).get();
    }

    @Override
    public Terminals RegisterTerminal(Terminals terminals) {
        Date date = new Date();
        terminals.setDateCreated(date.toString());
        return terminalRepository.save(terminals);
    }

    @Override
    public Terminals EditTerminal(Terminals terminals) {
        return terminalRepository.save(terminals);
    }

    @Override
    public Response uploadTerminals(MultipartFile uploadedFile) {
        List<Terminals> terminalsList = terminalRepository.findAll();
        int size = terminalsList.size();
        try
        {
            Response response = new Response();
            Date date = new Date();
            long mills = date.getTime();
            File file = new File(path+mills+"_terminalsUpload.xlsx");   //creating a new file instance
            uploadedFile.transferTo(file);
            List<Terminals> terminals = mapTerminalsDataFromFile(file);
            int sizeID = terminals.size();

            for (int i=0; i<sizeID; i++){
                terminals.get(i).setId(size+i+1);
                terminals.get(i).setDateCreated(date.toString());
                terminalRepository.save(terminals.get(i));

            }
            response.setRespBody(terminals);
            return response;
        }
        catch(Throwable e)
        {
            Response response = new Response();
            response.setRespBody(null);
            return response;
        }
    }

    private List<Terminals> mapTerminalsDataFromFile(File file) throws Throwable{

        List<Terminals> dtos = ExcelMapper.mapFromExcel(file)
                .toObjectOf(Terminals.class)
                .fromSheet(0) // if this method not used , called all sheets
                .map();
        return dtos;
    }
}