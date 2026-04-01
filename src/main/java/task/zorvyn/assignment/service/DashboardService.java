package task.zorvyn.assignment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import task.zorvyn.assignment.dto.CategorySummaryDto;
import task.zorvyn.assignment.dto.DashboardSummaryDto;
import task.zorvyn.assignment.entity.FinancialRecord;
import task.zorvyn.assignment.entity.FinancialRecordType;
import task.zorvyn.assignment.repository.CategoryTotalView;
import task.zorvyn.assignment.repository.FinancialRecordRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository financialRecordRepository;

    public DashboardSummaryDto getDashboardSummary() {
        BigDecimal totalIncome = getTotalIncome();
        BigDecimal totalExpenses = getTotalExpenses();

        // Net balance is defined as income - expenses. We keep this explicit
        // instead of re-querying DB to keep logic easy to read and maintain.
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        return DashboardSummaryDto.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryWiseTotals(getCategoryWiseTotals())
                .recentActivity(getRecentActivity())
                .build();
    }

    public BigDecimal getTotalIncome() {
        return financialRecordRepository.sumAmountByType(FinancialRecordType.INCOME);
    }

    public BigDecimal getTotalExpenses() {
        return financialRecordRepository.sumAmountByType(FinancialRecordType.EXPENSE);
    }

    public BigDecimal getNetBalance() {
        return getTotalIncome().subtract(getTotalExpenses());
    }

    public List<CategorySummaryDto> getCategoryWiseTotals() {
        List<CategoryTotalView> grouped = financialRecordRepository.summarizeAmountByCategory();

        return grouped.stream()
                .map(item -> CategorySummaryDto.builder()
                        .category(item.getCategory())
                        .total(item.getTotal())
                        .build())
                .toList();
    }

    public List<FinancialRecord> getRecentActivity() {
        // We read only non-deleted records so dashboard widgets stay consistent
        // with the rest of the application APIs.
        return financialRecordRepository.findTop5ByIsDeletedFalseOrderByCreatedAtDesc();
    }
}
