package task.zorvyn.assignment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task.zorvyn.assignment.dto.DashboardSummaryDto;
import task.zorvyn.assignment.service.DashboardService;

import java.math.BigDecimal;


@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary() {
        log.info("Fetching dashboard summary");
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/income-total")
    public ResponseEntity<BigDecimal> getTotalIncome() {
        log.info("Fetching total income");
        return ResponseEntity.ok(dashboardService.getTotalIncome());
    }

    @GetMapping("/expense-total")
    public ResponseEntity<BigDecimal> getTotalExpenses() {
        log.info("Fetching total expenses");
        return ResponseEntity.ok(dashboardService.getTotalExpenses());
    }

    @GetMapping("/net-balance")
    public ResponseEntity<BigDecimal> getNetBalance() {
        log.info("Fetching net balance");
        return ResponseEntity.ok(dashboardService.getNetBalance());
    }
}
