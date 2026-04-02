package task.zorvyn.assignment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;

    @Transactional
    public FinancialRecord createRecord(FinancialRecord record, Long createdByUserId) {
        if (record == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }
        log.info("Service call: create financial record for userId={}", createdByUserId);
        validateRecordPayload(record);

        User creatorUserEntity = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator user not found for id: " + createdByUserId));

        if (creatorUserEntity.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedActionException("Inactive users cannot create financial records");
        }

        record.setId(null);
        record.setCreatedBy(creatorUserEntity);
        record.setIsDeleted(false);

        return financialRecordRepository.save(record);
    }

    public FinancialRecord getRecordById(Long recordId) {
        log.info("Service call: fetch financial record by id={}", recordId);
        return financialRecordRepository.findByIdAndIsDeletedFalse(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found for id: " + recordId));
    }

    public Page<FinancialRecord> getRecords(Pageable pageable) {
        log.info("Service call: fetch paginated financial records");
        return financialRecordRepository.findAllByIsDeletedFalse(pageable);
    }

    public Page<FinancialRecord> getFilteredRecords(
            String category,
            FinancialRecordType type,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        log.info("Service call: fetch filtered records category={}, type={}, startDate={}, endDate={}", category, type, startDate, endDate);
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate cannot be after endDate");
        }

        String normalizedCategoryFilter = category == null ? null : category.trim();
        if (normalizedCategoryFilter != null && normalizedCategoryFilter.isBlank()) {
            normalizedCategoryFilter = null;
        }

        return financialRecordRepository.findByFilters(normalizedCategoryFilter, type, startDate, endDate, pageable);
    }

    @Transactional
    public FinancialRecord updateRecord(Long recordId, FinancialRecord payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }
        log.info("Service call: update financial record id={}", recordId);
        validateRecordPayload(payload);

        FinancialRecord existingFinancialRecordEntity = getRecordById(recordId);

        existingFinancialRecordEntity.setAmount(payload.getAmount());
        existingFinancialRecordEntity.setType(payload.getType());
        existingFinancialRecordEntity.setCategory(payload.getCategory().trim());
        existingFinancialRecordEntity.setDate(payload.getDate());
        existingFinancialRecordEntity.setNotes(payload.getNotes());

        return financialRecordRepository.save(existingFinancialRecordEntity);
    }

    @Transactional
    public void softDeleteRecord(Long recordId) {
        log.info("Service call: soft delete financial record id={}", recordId);
        FinancialRecord existingFinancialRecordEntity = getRecordById(recordId);

        existingFinancialRecordEntity.setIsDeleted(true);
        financialRecordRepository.save(existingFinancialRecordEntity);
    }

    private void validateRecordPayload(FinancialRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Financial record payload is required");
        }
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
        if (record.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Record date cannot be in the future");
        }
    }
}
