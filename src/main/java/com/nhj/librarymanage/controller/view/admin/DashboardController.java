package com.nhj.librarymanage.controller.view.admin;

import com.nhj.librarymanage.domain.annotations.Description;
import com.nhj.librarymanage.model.vo.DashboardStatistics;
import com.nhj.librarymanage.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/admin")
@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    @Description("대시보드")
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardStatistics dashboardStatistics = dashboardService.getBorrowStatistics();

        model.addAttribute("statistics", dashboardStatistics);

        return "admin/dashboard";
    }

}
