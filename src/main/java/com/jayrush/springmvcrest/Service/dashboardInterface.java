package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.domain.DashboardUtils;

/**
 * @author JoshuaO
 */
public interface dashboardInterface {
    DashboardUtils getDashboardUtils(String institutionID);
}
