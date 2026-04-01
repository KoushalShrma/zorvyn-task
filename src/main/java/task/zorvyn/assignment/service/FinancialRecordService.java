package task.zorvyn.assignment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import task.zorvyn.assignment.entity.FinancialRecord;
import task.zorvyn.assignment.entity.FinancialRecordType;
import task.zorvyn.assignment.entity.User;
import task.zorvyn.assignment.entity.UserStatus;
import task.zorvyn.assignment.exception.ResourceNotFoundException;
import task.zorvyn.assignment.exception.UnauthorizedActionException;
import task.zorvyn.assignment.repository.FinancialRecordRepository;
import task.zorvyn.assignment.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;

    @Transactional
    public FinancialRecord createRecord(FinancialRecord record, Long createdByUserId) {
        validateRecordPayload(record);

        User creator = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator user not found for id: " + createdByUserId));

        if (creator.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedActionException("Inactive users cannot create financial records");
        }

        // We always trust the authenticated creator source (service input), not client payload,
        // so consumers cannot spoof record ownership.
        record.setId(null);
        record.setCreatedBy(creator);
        record.setIsDeleted(false);

        return financialRecordRepository.save(record);
    }

    public FinancialRecord getRecordById(Long recordId) {
        return financialRecordRepository.findByIdAndIsDeletedFalse(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found for id: " + recordId));
    }

    public Page<FinancialRecord> getRecords(Pageable pageable) {
        return financialRecordRepository.findAllByIsDeletedFalse(pageable);
    }

    public Page<FinancialRecord> getFilteredRecords(
            String category,
            FinancialRecordType type,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate cannot be after endDate");
        }

        String normalizedCategory = category == null ? null : category.trim();
        if (normalizedCategory != null && normalizedCategory.isBlank()) {
            normalizedCategory = null;
        }

        return financialRecordRepository.findByFilters(normalizedCategory, type, startDate, endDate, pageable);
    }

    @Transactional
    public FinancialRecord updateRecord(Long recordId, FinancialRecord payload) {
        validateRecordPayload(payload);

        FinancialRecord existing = getRecordById(recordId);

        existing.setAmount(payload.getAmount());
        existing.setType(payload.getType());
        existing.setCategory(payload.getCategory().trim());
        existing.setDate(payload.getDate());
        existing.setNotes(payload.getNotes());

        return financialRecordRepository.save(existing);
    }

    @Transactional
    public void softDeleteRecord(Long recordId) {
        FinancialRecord existing = getRecordById(recordId);

        // Soft delete preserves history for dashboards/audits while preventing
        // the record from showing up in normal read APIs.
        existing.setIsDeleted(true);
        financialRecordRepository.save(existing);
    }

    private void validateRecordPayload(FinancialRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Financial record payload is required");
        }
        // Finance amounts must be strictly positive to avoid ambiguous zero-value entries.
        if (record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (record.getType() == null) {
            throw new IllegalArgumentException("Record type is required");
        }
        if (record.getCategory() == null || record.getCategory().trim().isBlank()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (record.getDate() == null) {
            throw new IllegalArgumentException("Date is required");
        }
        // We reject future-dated records to keep financial reporting period-safe
        // and to prevent accidental/intentional projection data in historical books.
        if (record.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Record date cannot be in the future");
        }
    }
}
