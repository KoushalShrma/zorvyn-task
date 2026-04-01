package task.zorvyn.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import task.zorvyn.assignment.entity.FinancialRecord;
import task.zorvyn.assignment.entity.FinancialRecordType;
import task.zorvyn.assignment.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    List<FinancialRecord> findAllByIsDeletedFalse();

    Page<FinancialRecord> findAllByIsDeletedFalse(Pageable pageable);

    Optional<FinancialRecord> findByIdAndIsDeletedFalse(Long id);

    List<FinancialRecord> findAllByIsDeletedFalseAndDateBetween(LocalDate startDate, LocalDate endDate);

    List<FinancialRecord> findAllByCreatedByAndIsDeletedFalse(User createdBy);

    List<FinancialRecord> findTop5ByIsDeletedFalseOrderByCreatedAtDesc();

    @Query("""
            SELECT fr
            FROM FinancialRecord fr
            WHERE fr.isDeleted = false
              AND (:category IS NULL OR LOWER(fr.category) = LOWER(:category))
              AND (:type IS NULL OR fr.type = :type)
              AND (:startDate IS NULL OR fr.date >= :startDate)
              AND (:endDate IS NULL OR fr.date <= :endDate)
            """)
    Page<FinancialRecord> findByFilters(
            @Param("category") String category,
            @Param("type") FinancialRecordType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
            SELECT COALESCE(SUM(fr.amount), 0)
            FROM FinancialRecord fr
            WHERE fr.type = :type
              AND fr.isDeleted = false
            """)
    BigDecimal sumAmountByType(@Param("type") FinancialRecordType type);

    @Query("""
            SELECT COALESCE(SUM(fr.amount), 0)
            FROM FinancialRecord fr
            WHERE fr.type = :type
              AND fr.isDeleted = false
              AND fr.date BETWEEN :startDate AND :endDate
            """)
    BigDecimal sumAmountByTypeInDateRange(
            @Param("type") FinancialRecordType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT fr.type AS type, COALESCE(SUM(fr.amount), 0) AS total
            FROM FinancialRecord fr
            WHERE fr.isDeleted = false
              AND fr.date BETWEEN :startDate AND :endDate
            GROUP BY fr.type
            """)
    List<RecordTypeTotalView> summarizeAmountByTypeInDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT fr.category AS category, COALESCE(SUM(fr.amount), 0) AS total
            FROM FinancialRecord fr
            WHERE fr.isDeleted = false
            GROUP BY fr.category
            ORDER BY fr.category ASC
            """)
    List<CategoryTotalView> summarizeAmountByCategory();
}
