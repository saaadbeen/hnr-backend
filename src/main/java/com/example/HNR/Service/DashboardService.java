package com.example.HNR.Service;

import com.example.HNR.DTO.DashboardStatsDTO;
import com.example.HNR.DTO.PrefectureStatsDTO;
import java.util.Date;
import java.util.List;

public interface DashboardService {
    DashboardStatsDTO getDashboardStats();
    DashboardStatsDTO getDashboardStatsByPrefecture(String prefecture);
    DashboardStatsDTO getDashboardStatsByDateRange(Date startDate, Date endDate);
    List<PrefectureStatsDTO> getPrefectureStats();
}