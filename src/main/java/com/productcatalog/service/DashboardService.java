package com.productcatalog.service;

import com.productcatalog.dto.dashboard.DashboardStatsDto;

public interface DashboardService {

    DashboardStatsDto getDashboardStats();

    DashboardStatsDto getPublicStats();
}
