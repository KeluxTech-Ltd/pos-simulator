package com.jayrush.springmvcrest.rolesPermissions.dtos;

import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.roleType;
import com.jayrush.springmvcrest.rolesPermissions.models.Permissions;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Collection;

/**
 * @author JoshuaO
 */

@Data
public class rolesDto {
    private String name;
    private String email;
    private String description;
    private Collection<Permissions> permissions;
    private Institution institution;
}
