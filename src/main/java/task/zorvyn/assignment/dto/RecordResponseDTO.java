package task.zorvyn.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import task.zorvyn.assignment.entity.FinancialRecordType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordResponseDTO {
    private Long id;
    private BigDecimal amount;
    private FinancialRecordType type;
    private String category;
    private LocalDate date;
    private String notes;
    private Long createdByUserId;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
}
