package com.jayrush.springmvcrest.serviceProviders.services;

import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.domainDTO.DeleteUser;
import com.jayrush.springmvcrest.serviceProviders.Models.profiles;
import com.jayrush.springmvcrest.serviceProviders.Models.serviceProviders;

import java.util.List;

/**
 * @author JoshuaO
 */
public interface serviceProviderService {
    List<serviceProviders>getAllProviders();
    List<profiles>getAllProfiles();
    List<profiles>getAllProfilesbyProvidersID(Long id);
    serviceProviders addProvider(serviceProviders serviceProviders);
    serviceProviders addProfiles(profiles profiles);
    profiles editProfiles(profiles profiles);
    serviceProviders editProviders(serviceProviders serviceProviders);
    profiles getProfilebyID(Long id);
    serviceProviders getProviderbyID(Long id);
    Response deleteProvider(DeleteUser deleteUser);
    Response deleteProfile(DeleteUser deleteUser);
}
