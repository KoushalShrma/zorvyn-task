package task.zorvyn.assignment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import task.zorvyn.assignment.dto.RecordRequestDTO;
import task.zorvyn.assignment.dto.RecordResponseDTO;
import task.zorvyn.assignment.entity.FinancialRecord;
import task.zorvyn.assignment.entity.FinancialRecordType;
import task.zorvyn.assignment.service.FinancialRecordService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService financialRecordService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecordResponseDTO> createRecord(@Valid @RequestBody RecordRequestDTO request) {
        if (request.getCreatedByUserId() == null) {
            throw new IllegalArgumentException("createdByUserId is required for creating a record");
        }

        FinancialRecord created = financialRecordService.createRecord(
                toEntity(request),
                request.getCreatedByUserId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<RecordResponseDTO> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(financialRecordService.getRecordById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<Page<RecordResponseDTO>> getRecords(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) FinancialRecordType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<FinancialRecord> page;
        if (category != null || type != null || startDate != null || endDate != null) {
            page = financialRecordService.getFilteredRecords(category, type, startDate, endDate, pageable);
        } else {
            page = financialRecordService.getRecords(pageable);
        }

        return ResponseEntity.ok(page.map(this::toResponse));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecordResponseDTO> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody RecordRequestDTO request
    ) {
        FinancialRecord updated = financialRecordService.updateRecord(id, toEntity(request));
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> softDeleteRecord(@PathVariable Long id) {
        financialRecordService.softDeleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    private FinancialRecord toEntity(RecordRequestDTO request) {
        return FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .build();
    }

    private RecordResponseDTO toResponse(FinancialRecord record) {
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
