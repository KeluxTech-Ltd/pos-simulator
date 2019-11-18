package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.domain.domainDTO.LoginDTO;
import com.jayrush.springmvcrest.domain.domainDTO.LoginDTOresponse;
import com.jayrush.springmvcrest.domain.domainDTO.tmsUserDTO;
import com.jayrush.springmvcrest.domain.tmsUser;
import com.jayrush.springmvcrest.exceptions.tmsExceptions;
import com.jayrush.springmvcrest.jwt.JwtTokenUtil;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class superAdminLoginServiceImpl implements superAdminLoginService {
    @Autowired
    InstitutionRepository institutionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Override
    public Response superAdminLogin(LoginDTO loginDTO) {
        Response response = new Response();
        tmsUser User = userRepository.findByusername(loginDTO.getUsername());

        if(User != null){
            if(!passwordEncoder.matches(loginDTO.getPassword(),User.getPassword())){
                throw new tmsExceptions("Invalid User Password");
            }
            tmsUserDTO UserDTO = new tmsUserDTO();
            String token = jwtTokenUtil.generateToken(User.getUsername());
            UserDTO.setAuthToken(token);
            UserDTO.setInstitution(null);
            response.setRespCode("00");
            response.setRespDescription("Success");
            response.setRespBody(UserDTO);
            return response;
        }else{
            throw new tmsExceptions("Invalid User Name");
        }
    }


    @Override
    public Institution superAdminCreateInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }


    @Override
    public List<Institution> superAdminViewInstitutions() {
        return institutionRepository.findAll();
    }

    @Override
    public List<tmsUser> superAdminViewUsers() {
        return userRepository.findAll();
    }

    @Override
    public tmsUser superAdminCreateUsers(tmsUser tmsUser) {
        tmsUser.setPassword(passwordEncoder.encode(tmsUser.getPassword()));

        return userRepository.save(tmsUser);
    }

    @Override
    public tmsUser superAdminGetUser(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public Institution superAdminGetInstitution(Long id) {
        return institutionRepository.findById(id).get();
    }

    @Override
    public void superAdminDeleteUsers(tmsUser tmsUser) {
        userRepository.delete(tmsUser);
    }
}
