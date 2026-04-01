package task.zorvyn.assignment.repository;

import task.zorvyn.assignment.entity.FinancialRecordType;

import java.math.BigDecimal;

public interface RecordTypeTotalView {
    FinancialRecordType getType();

    BigDecimal getTotal();
}
