package task.zorvyn.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import task.zorvyn.assignment.entity.FinancialRecord;
import task.zorvyn.assignment.entity.FinancialRecordType;
import task.zorvyn.assignment.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    List<FinancialRecord> findAllByIsDeletedFalse();

    List<FinancialRecord> findAllByIsDeletedFalseAndDateBetween(LocalDate startDate, LocalDate endDate);

    List<FinancialRecord> findAllByCreatedByAndIsDeletedFalse(User createdBy);

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
}
