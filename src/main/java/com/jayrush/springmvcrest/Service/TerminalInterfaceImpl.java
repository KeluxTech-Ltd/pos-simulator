package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.Terminals;
import com.jayrush.springmvcrest.domain.domainDTO.PagedInstitutionRequestDTO;
import com.jayrush.springmvcrest.domain.domainDTO.PagedRequestDTO;
import com.jayrush.springmvcrest.domain.domainDTO.TerminalListDTO;
import io.github.mapper.excel.ExcelMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class TerminalInterfaceImpl implements TerminalInterface {
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

    @Override
    public TerminalListDTO getPagenatedTerminals(PagedRequestDTO pagedTerminalsDTO) {
        TerminalListDTO terminalListDTO = new TerminalListDTO();
        List<Terminals> TerminalResp;
        Page<Terminals> pagedTerminals;
        Pageable paged;

        if (pagedTerminalsDTO.getSize()>0 && pagedTerminalsDTO.getPage()>=0){
            paged = PageRequest.of(pagedTerminalsDTO.getPage(),pagedTerminalsDTO.getSize());
        }
        else {
            paged = PageRequest.of(0,1000000);
        }

        pagedTerminals = terminalRepository.findAll(paged);
        TerminalResp = pagedTerminals.getContent();
        if (pagedTerminals!=null && pagedTerminals.getContent().size()>0){
            terminalListDTO.setHasNextRecord(pagedTerminals.hasNext());
            terminalListDTO.setTotalCount((int) pagedTerminals.getTotalElements());
        }
        terminalListDTO.setTerminals(TerminalResp);

        return terminalListDTO;
    }

    @Override
    public TerminalListDTO getPagenatedTerminalsByInstitution(PagedInstitutionRequestDTO institution) {
        TerminalListDTO terminalListDTO = new TerminalListDTO();
        List<Terminals> TerminalResp;
        Page<Terminals> pagedTerminals;
        Pageable paged;

        if (institution.getSize()>0 && institution.getPage()>=0){
            paged = PageRequest.of(institution.getPage(),institution.getSize());
        }
        else {
            paged = PageRequest.of(0,1000000);
        }

        pagedTerminals = terminalRepository.findByInstitution_Id(institution.getInstitutionID(),paged);
//        pagedTerminals = terminalRepository.findAll(paged);
        TerminalResp = pagedTerminals.getContent();
        if (pagedTerminals!=null && pagedTerminals.getContent().size()>0){
            terminalListDTO.setHasNextRecord(pagedTerminals.hasNext());
            terminalListDTO.setTotalCount((int) pagedTerminals.getTotalElements());
        }
        terminalListDTO.setTerminals(TerminalResp);

        return terminalListDTO;
    }
}