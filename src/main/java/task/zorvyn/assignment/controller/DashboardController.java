package task.zorvyn.assignment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task.zorvyn.assignment.dto.DashboardSummaryDto;
import task.zorvyn.assignment.service.DashboardService;

import java.math.BigDecimal;

/**
 * Dashboard summary endpoints.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/income-total")
    public ResponseEntity<BigDecimal> getTotalIncome() {
        return ResponseEntity.ok(dashboardService.getTotalIncome());
    }

    @GetMapping("/expense-total")
    public ResponseEntity<BigDecimal> getTotalExpenses() {
        return ResponseEntity.ok(dashboardService.getTotalExpenses());
    }

    @GetMapping("/net-balance")
    public ResponseEntity<BigDecimal> getNetBalance() {
        return ResponseEntity.ok(dashboardService.getNetBalance());
    }
}
