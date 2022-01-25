package com.jayrush.springmvcrest.commission.service;

import com.jayrush.springmvcrest.commission.model.dtos.CommissionListDTO;
import com.jayrush.springmvcrest.commission.model.dtos.CommissionsHistoryDTO;

/**
 * @author JoshuaO
 */
public interface commissionService {
    CommissionListDTO getCommissionHistory(CommissionsHistoryDTO commissionsHistoryDTO);

}
