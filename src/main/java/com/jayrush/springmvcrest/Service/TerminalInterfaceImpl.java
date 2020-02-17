package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.Repositories.terminalKeysRepo;
import com.jayrush.springmvcrest.domain.*;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.domainDTO.*;
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
import java.util.*;

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

    @Autowired
    terminalKeysRepo terminalKeysRepo;



    @Override
    public List<Terminals> getAllTerminals() {
        return terminalRepository.findAll();
    }

    @Override
    public List<Terminals> getTerminalsbyInstitution(String institutionID) {
        return terminalRepository.findByInstitution_InstitutionID(institutionID);
    }

    @Override
    public Terminals getTerminalByID(Long id) {
        return terminalRepository.findById(id).get();
    }

    @Override
    public Terminals RegisterTerminal(TerminalsDTO terminals) {
        Terminals t = new Terminals();
        Terminals terminals1 = terminalRepository.findByterminalID(terminals.getTerminalID());
        profiles p = profilesServiceRepo.findByProfileName(terminals.getProfileName());
        Institution i = institutionRepository.findByInstitutionID(terminals.getInstitutionID());
        if (Objects.nonNull(terminals1)){
            t.setSavedDescription("Terminal ID already exists");
            t.setSaved(false);
        }
        else if (Objects.isNull(p)){
            t.setSaved(false);
            t.setSavedDescription("No profile for terminal");
        }
        else if(Objects.isNull(i)){
            t.setSaved(false);
            t.setSavedDescription("Institution not found");
        }
        else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String date = simpleDateFormat.format(new Date());
            terminals.setDateCreated(date);
            t.setInstitution(i);
            t.setProfile(p);
            t.setTerminalSerialNo(terminals.getTerminalSerialNo());
            t.setTerminalID(terminals.getTerminalID().toUpperCase());
            t.setTerminalType(terminals.getTerminalType());
            t.setTerminalROMVersion(terminals.getTerminalROMVersion());
            t.setDateCreated(date);
            if ((t.getTerminalType().equals(terminalType.TOPWISE.toString())))
            {
                t.setTerminalType(terminalType.TOPWISE.toString());
                terminalRepository.save(t);
                t.setSaved(true);
                logger.info("saved to db {} ",t);

            }
            else if ((t.getTerminalType().equals(terminalType.PAX.toString())))
            {
                t.setTerminalType(terminalType.PAX.toString());
                terminalRepository.save(t);
                t.setSaved(true);
                logger.info("saved to db {} ",t);

            }
            else if ((t.getTerminalType().equals(terminalType.TELPO.toString())))
            {
                t.setTerminalType(terminalType.TELPO.toString());
                terminalRepository.save(t);
                t.setSaved(true);
                logger.info("saved to db {} ",t);

            }
            else {
                logger.info("Terminal type not found for {}",t.getTerminalType());
                t.setSaved(false);
                t.setSavedDescription("Terminal type not found");

            }
        }

        return t;
    }

    @Override
    public Terminals EditTerminal(Terminals terminals) {
        return terminalRepository.save(terminals);
    }

    @Override
    public Response uploadTerminals(MultipartFile uploadedFile) {
        try
        {
            Response response = new Response();
            List<uploadResponseDTO> uploadResponse  = new ArrayList<>();

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
                uploadResponseDTO uploadResponseStatus = new uploadResponseDTO();
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
                    t.setSavedDescription("Terminal ID already Exists");
                    terminal.add(i,t);

                    uploadResponseStatus.setSavedStatus(false);
                    uploadResponseStatus.setStatusDescription("Terminal ID already Exists");
                    uploadResponseStatus.setTerminalID(t.getTerminalID());
                    uploadResponse.add(i,uploadResponseStatus);
                }
                else if (Objects.isNull(profile)){
                    logger.info("No profile Exists for {}",terminals.get(i).getProfileName());
                    t.setSaved(false);
                    t.setSavedDescription("No profile Exists");
                    uploadResponseStatus.setSavedStatus(false);
                    uploadResponseStatus.setStatusDescription("No profile Exists");
                    uploadResponseStatus.setTerminalID(t.getTerminalID());
                    uploadResponse.add(i,uploadResponseStatus);
                    terminal.add(i,t);
                }
                else if (Objects.isNull(institution)){
                    logger.info("No Institution Exists for {}",terminals.get(i).getInstitutionID());
                    t.setSaved(false);
                    t.setSavedDescription("No Institution Exists");
                    uploadResponseStatus.setSavedStatus(false);
                    uploadResponseStatus.setStatusDescription("No Institution Exists");
                    uploadResponseStatus.setTerminalID(t.getTerminalID());
                    uploadResponse.add(i,uploadResponseStatus);
                    terminal.add(i,t);
                }
                else {
                    if ((t.getTerminalType().equals(terminalType.TOPWISE.toString())))
                    {
                        terminalRepository.save(t);
                        t.setSaved(true);
                        logger.info("saved to db {} ",t);
                        uploadResponseStatus.setSavedStatus(true);
                        uploadResponseStatus.setStatusDescription("Uploaded Successfully");
                        uploadResponseStatus.setTerminalID(t.getTerminalID());
                        uploadResponse.add(i,uploadResponseStatus);
                        terminal.add(i,t);
                    }
                    else if ((t.getTerminalType().equals(terminalType.PAX.toString())))
                    {
                        terminalRepository.save(t);
                        t.setSaved(true);
                        uploadResponseStatus.setSavedStatus(true);
                        uploadResponseStatus.setStatusDescription("Uploaded Successfully");
                        uploadResponseStatus.setTerminalID(t.getTerminalID());
                        uploadResponse.add(i,uploadResponseStatus);
                        logger.info("saved to db {} ",t);
                        terminal.add(i,t);
                    }
                    else if ((t.getTerminalType().equals(terminalType.TELPO.toString())))
                    {
                        terminalRepository.save(t);
                        t.setSaved(true);
                        uploadResponseStatus.setSavedStatus(true);
                        uploadResponseStatus.setStatusDescription("Uploaded Successfully");
                        uploadResponseStatus.setTerminalID(t.getTerminalID());
                        uploadResponse.add(i,uploadResponseStatus);
                        logger.info("saved to db {} ",t);
                        terminal.add(i,t);
                    }
                    else {
                        logger.info("Terminal type not found for {}",t.getTerminalType());
                        t.setSaved(false);
                        uploadResponseStatus.setSavedStatus(false);
                        uploadResponseStatus.setStatusDescription("Terminal Type not found");
                        uploadResponseStatus.setTerminalID(t.getTerminalID());
                        uploadResponse.add(i,uploadResponseStatus);
                        t.setSavedDescription("Terminal type not found");
                        terminal.add(i,t);
                    }
                }
            }
            response.setRespBody(uploadResponse);
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
        if (pagedTerminals.getContent().size() > 0){
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
        TerminalResp = pagedTerminals.getContent();
        if (pagedTerminals.getContent().size() > 0){
            terminalListDTO.setHasNextRecord(pagedTerminals.hasNext());
            terminalListDTO.setTotalCount((int) pagedTerminals.getTotalElements());
        }
        terminalListDTO.setTerminals(TerminalResp);

        return terminalListDTO;
    }

    @Override
    public List<profiles> getProfilesByInstitutionName(String institutionName) {
        Institution institution = institutionRepository.findByinstitutionName(institutionName);
        if (Objects.nonNull(institution)){
            return institution.getServiceProviders().getProfile();
        }
        return Collections.emptyList() ;
    }

//    @Override
//    public void getKeysForTerminal(String terminalID) {
//        Terminals terminal = terminalRepository.findByterminalID(terminalID);
//
//        if (Objects.nonNull(terminal)){
//            terminalKeyManagement keys = terminalKeysRepo.findByTerminalID(terminal.getTerminalID());
//            if (Objects.nonNull(keys)){
//                terminalKeyManagement key = keyManagement(terminal);
//                keys.setLastExchangeDateTime(key.getLastExchangeDateTime());
//                keys.setTerminalID(key.getTerminalID());
//                keys.setPinKey(key.getPinKey());
//                keys.setSessionKey(key.getSessionKey());
//                keys.setMasterKey(key.getMasterKey());
//                keys.setParameterDownloaded(key.getParameterDownloaded());
//                terminalKeysRepo.save(keys);
//            }else{
//                terminalKeyManagement terminalKeyManagement = new terminalKeyManagement();
//                terminalKeyManagement key = keyManagement(terminal);
//                terminalKeyManagement.setLastExchangeDateTime(key.getLastExchangeDateTime());
//                terminalKeyManagement.setTerminalID(key.getTerminalID());
//                terminalKeyManagement.setPinKey(key.getPinKey());
//                terminalKeyManagement.setSessionKey(key.getSessionKey());
//                terminalKeyManagement.setMasterKey(key.getMasterKey());
//                terminalKeyManagement.setParameterDownloaded(key.getParameterDownloaded());
//                terminalKeysRepo.save(terminalKeyManagement);
//            }
//
//
//
//        }
//        else {
//            logger.info("terminal Id not found to perform key exchange");
//        }
//    }
}