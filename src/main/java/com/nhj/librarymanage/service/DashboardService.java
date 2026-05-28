package com.nhj.librarymanage.service;

import com.nhj.librarymanage.model.vo.DashboardStatistics;
import com.nhj.librarymanage.repository.DashboardQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DashboardService {

    private final DashboardQueryRepository dashboardQueryRepository;

    public DashboardStatistics getTodayBorrowStatistics() {
        return dashboardQueryRepository.getCurrentBorrowStatistics();
    }

}
