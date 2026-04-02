package task.zorvyn.assignment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import task.zorvyn.assignment.dto.CategorySummaryDto;
import task.zorvyn.assignment.dto.DashboardSummaryDto;
import task.zorvyn.assignment.dto.RecordResponseDTO;
import task.zorvyn.assignment.entity.FinancialRecord;
import task.zorvyn.assignment.entity.FinancialRecordType;
import task.zorvyn.assignment.repository.CategoryTotalView;
import task.zorvyn.assignment.repository.FinancialRecordRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dashboard aggregation logic.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository financialRecordRepository;

    public DashboardSummaryDto getDashboardSummary() {
        BigDecimal totalIncome = getTotalIncome();
        BigDecimal totalExpenses = getTotalExpenses();

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

    public List<RecordResponseDTO> getRecentActivity() {
        return financialRecordRepository.findTop5ByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::toRecordResponseDto)
                .toList();
    }

    private RecordResponseDTO toRecordResponseDto(FinancialRecord record) {
        return RecordResponseDTO.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .date(record.getDate())
                .notes(record.getNotes())
                .createdByUserId(record.getCreatedBy() != null ? record.getCreatedBy().getId() : null)
                .createdByUsername(record.getCreatedBy() != null ? record.getCreatedBy().getUsername() : null)
                .createdAt(record.getCreatedAt())
                .lastModifiedAt(record.getLastModifiedAt())
                .build();
    }
}
