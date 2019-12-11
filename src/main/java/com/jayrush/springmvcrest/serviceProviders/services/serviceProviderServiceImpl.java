package com.jayrush.springmvcrest.serviceProviders.services;

import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.domainDTO.DeleteUser;
import com.jayrush.springmvcrest.domain.tmsUser;
import com.jayrush.springmvcrest.serviceProviders.Models.profiles;
import com.jayrush.springmvcrest.serviceProviders.Models.serviceProviders;
import com.jayrush.springmvcrest.serviceProviders.repository.profilesServiceRepo;
import com.jayrush.springmvcrest.serviceProviders.repository.serviceProviderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author JoshuaO
 */
@Service
public class serviceProviderServiceImpl implements serviceProviderService {
    @Autowired
    serviceProviderRepo serviceProviderRepo;

    @Autowired
    profilesServiceRepo profilesServiceRepo;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public List<serviceProviders> getAllProviders() {
        return serviceProviderRepo.findAll();
    }

    @Override
    public List<profiles> getAllProfiles() {
        return profilesServiceRepo.findAll();
    }
    @Override
    public List<profiles> getAllProfilesbyProvidersID(Long id) {
        serviceProviders providers = serviceProviderRepo.findById(id).get();
        List<profiles> profilesList = providers.getProfile();
        return profilesList;
    }

    @Override
    public Response addProvider(serviceProviders serviceProviders) {
        serviceProviders serviceProviders1 = new serviceProviders();
        serviceProviders = serviceProviderRepo.findByProviderName(serviceProviders.getProviderName());
        Response response = new Response();
        if (Objects.isNull(serviceProviders1)){
            serviceProviders1 = serviceProviderRepo.save(serviceProviders);
            response.setRespBody(serviceProviders1);
            response.setRespDescription("Success");
            response.setRespCode("00");
            return response;
        }
        else {
            response.setRespCode("96");
            response.setRespBody("Service Provider already Exists");
            return response;
        }

    }

    @Override
    public serviceProviders addProfiles(profiles profiles) {
        Long id = profiles.getServiceProviders().getId();
        serviceProviders providers = serviceProviderRepo.findById(id).get();

        List<profiles>profilesList = providers.getProfile();

        profiles profiles1 = new profiles();
        profiles1.setPort(profiles.getPort());
        profiles1.setProfileIP(profiles.getProfileIP());
        profiles1.setProfileName(profiles.getProfileName());
        profiles1.setZpk(profiles.getZpk());


        int index = profilesList.size();

        profilesList.add(index,profiles1);
        providers.setProfile(profilesList);
        return serviceProviderRepo.save(providers);
    }

    @Override
    public profiles editProfiles(profiles profiles) {
        return profilesServiceRepo.save(profiles);
    }

    @Override
    public serviceProviders editProviders(serviceProviders serviceProviders) {
        return serviceProviderRepo.save(serviceProviders);
    }

    @Override
    public profiles getProfilebyID(Long id) {
        return profilesServiceRepo.findById(id).get();
    }
    @Override
    public serviceProviders getProviderbyID(Long id) {
        return serviceProviderRepo.findById(id).get();
    }

    @Override
    public Response deleteProvider(DeleteUser deleteUser) {
        Response response = new Response();
        tmsUser tmsUser = userRepository.findByusername(deleteUser.getUsername());
        if (!tmsUser.equals(null)){
            if (passwordEncoder.matches(deleteUser.getPassword(),tmsUser.getPassword())){
                serviceProviders providers = serviceProviderRepo.findById(deleteUser.getIdToDelete()).get();
                serviceProviderRepo.delete(providers);
                response.setRespCode("00");
                response.setRespDescription("Success");
                return response;
            }
            else {
                response.setRespCode("55");
                response.setRespDescription("No User Found");
                return response;
            }
        }
        else {
            response.setRespCode("96");
            response.setRespDescription("No User Found");
            return response;
        }
    }

    @Override
    public Response deleteProfile(DeleteUser deleteUser) {
        Response response = new Response();
        tmsUser tmsUser = userRepository.findByusername(deleteUser.getUsername());
        if (!tmsUser.equals(null)){
            if (passwordEncoder.matches(deleteUser.getPassword(),tmsUser.getPassword())){
                profiles profiles = profilesServiceRepo.findById(deleteUser.getIdToDelete()).get();
                profilesServiceRepo.delete(profiles);
                response.setRespCode("00");
                response.setRespDescription("Success");
                return response;
            }
            else {
                System.out.println("Incorrect Password");
                response.setRespCode("55");
                response.setRespDescription("Incorrect User details");
                return response;
            }
        }
        else {
            System.out.println("Invalid User");
            response.setRespCode("96");
            response.setRespDescription("Invalid User");
            return response;
        }
    }
}
