package com.jayrush.springmvcrest.jwt;

import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.domainDTO.InstitutionDTO;
import com.jayrush.springmvcrest.domain.tmsUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.List;


public final class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(tmsUser user, String ipAddress) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                ipAddress,
                mapToGrantedAuthorities(user),
                true
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(tmsUser user) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.NO_AUTHORITIES;
        return grantedAuthorities;
    }
}
