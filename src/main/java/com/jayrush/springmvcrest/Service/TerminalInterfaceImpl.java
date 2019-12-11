package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.Terminals;
import com.jayrush.springmvcrest.domain.domainDTO.PagedInstitutionRequestDTO;
import com.jayrush.springmvcrest.domain.domainDTO.PagedRequestDTO;
import com.jayrush.springmvcrest.domain.domainDTO.TerminalListDTO;
import com.jayrush.springmvcrest.domain.domainDTO.TerminalsDTO;
import com.jayrush.springmvcrest.domain.terminalType;
import com.jayrush.springmvcrest.serviceProviders.Models.profiles;
import com.jayrush.springmvcrest.serviceProviders.repository.profilesServiceRepo;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@PropertySource("classpath:application.properties")
public class TerminalInterfaceImpl implements TerminalInterface {
    private static final Logger logger = LoggerFactory.getLogger(TerminalInterfaceImpl.class);

    @Value("${file-path}")
    private String path;


    @Autowired
    TerminalRepository terminalRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    profilesServiceRepo profilesServiceRepo;

    @Autowired
    InstitutionRepository institutionRepository;



    @Override
    public List<Terminals> getAllTerminals() {
        return terminalRepository.findAll();
    }

    @Override
    public Terminals getTerminalByID(Long id) {
        return terminalRepository.findById(id).get();
    }

    @Override
    public Terminals RegisterTerminal(TerminalsDTO terminals) {
        profiles p = profilesServiceRepo.findByProfileName(terminals.getProfileName());
        Institution i = institutionRepository.findByInstitutionID(terminals.getInstitutionID());
        Terminals t = new Terminals();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        terminals.setDateCreated(date);
        t.setInstitution(i);
        t.setProfile(p);
        t.setTerminalSerialNo(terminals.getTerminalSerialNo());
        t.setTerminalID(terminals.getTerminalID());
        t.setTerminalType(terminals.getTerminalType());
        t.setTerminalROMVersion(terminals.getTerminalROMVersion());
        t.setDateCreated(date);
        t.setSaved(true);
        return terminalRepository.save(t);
    }

    @Override
    public Terminals EditTerminal(Terminals terminals) {
        return terminalRepository.save(terminals);
    }

    @Override
    public Response uploadTerminals(MultipartFile uploadedFile) {
        List<Terminals> terminalsList = terminalRepository.findAll();
        try
        {
            Response response = new Response();

            Date date = new Date();
            long mills = date.getTime();
            File file = new File(path+mills+"_terminalsUpload.xlsx");   //creating a new file instance
            uploadedFile.transferTo(file);
            List<TerminalsDTO> terminals = mapTerminalsDataFromFile(file);
            List<Terminals> terminal = new ArrayList<>();
            int sizeID = terminals.size();

            for (int i=0; i<sizeID; i++)
            {
                Terminals t = new Terminals();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String Date = simpleDateFormat.format(new Date());
                terminals.get(i).setDateCreated(Date);
                t.setDateCreated(terminals.get(i).getDateCreated());
                t.setTerminalROMVersion(terminals.get(i).getTerminalROMVersion());
                t.setTerminalType(terminals.get(i).getTerminalType().toUpperCase());
                t.setTerminalID(terminals.get(i).getTerminalID());
                t.setTerminalSerialNo(terminals.get(i).getTerminalSerialNo());

                profiles profile = profilesServiceRepo.findByProfileName(terminals.get(i).getProfileName());
                Institution institution = institutionRepository.findByInstitutionID(terminals.get(i).getInstitutionID());
                t.setProfile(profile);
                t.setInstitution(institution);

                Terminals terminals1 = terminalRepository.findByTerminalIDAndInstitution_InstitutionID(terminals.get(i).getTerminalID(),
                        terminals.get(i).getInstitutionID());

                if (Objects.nonNull(terminals1)){
                    logger.info("Terminal ID already Exists for {}",t.getTerminalID());
                    t.setSaved(false);
                    terminal.add(i,t);
                }
                else if (Objects.isNull(profile)){
                    logger.info("No profile Exists for {}",profile.getProfileName());
                    t.setSaved(false);
                    terminal.add(i,t);
                }
                else if (Objects.isNull(institution)){
                    logger.info("No Institution Exists for {}",institution.getInstitutionID());
                    t.setSaved(false);
                    terminal.add(i,t);
                }
                else {
                    if ((t.getTerminalType().equals(terminalType.TOPWISE.toString()))){
                        terminalRepository.save(t);
                        t.setSaved(true);
                        logger.info("saved to db {} ",t);
                        terminal.add(i,t);
                    }
                    else if ((t.getTerminalType().equals(terminalType.PAX.toString()))){
                        terminalRepository.save(t);
                        t.setSaved(true);
                        logger.info("saved to db {} ",t);
                        terminal.add(i,t);
                    }
                    else if ((t.getTerminalType().equals(terminalType.TELPO.toString()))){
                        terminalRepository.save(t);
                        t.setSaved(true);
                        logger.info("saved to db {} ",t);
                        terminal.add(i,t);
                    }
                    else {
                        logger.info("Terminal type not found for {}",t.getTerminalType());
                        t.setSaved(false);
                        terminal.add(i,t);

                    }

                }
            }
            response.setRespBody(terminal);
            return response;
        }
        catch(Throwable e)
        {
            Response response = new Response();
            response.setRespCode("96");
            response.setRespBody(null);
            return response;
        }
    }



    private List<TerminalsDTO> mapTerminalsDataFromFile(File file) throws Throwable{

        List<TerminalsDTO> dtos = ExcelMapper.mapFromExcel(file)
                .toObjectOf(TerminalsDTO.class)
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