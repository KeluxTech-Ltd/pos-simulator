package com.jayrush.springmvcrest.domain;

import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author JoshuaO
 */

@Data
public class topFiveInstitutionDTO {
    List<String> institutionID;
}
