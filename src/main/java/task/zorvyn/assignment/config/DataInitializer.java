package task.zorvyn.assignment.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import task.zorvyn.assignment.entity.FinancialRecord;
import task.zorvyn.assignment.entity.FinancialRecordType;
import task.zorvyn.assignment.entity.Role;
import task.zorvyn.assignment.entity.User;
import task.zorvyn.assignment.entity.UserStatus;
import task.zorvyn.assignment.repository.FinancialRecordRepository;
import task.zorvyn.assignment.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FinancialRecordRepository financialRecordRepository;
        private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0 || financialRecordRepository.count() > 0) {
            return;
        }

        User viewer = userRepository.save(User.builder()
                .username("viewer.user")
                .password(passwordEncoder.encode("viewer@123"))
                .role(Role.VIEWER)
                .status(UserStatus.ACTIVE)
                .build());

        User analyst = userRepository.save(User.builder()
                .username("analyst.user")
                .password(passwordEncoder.encode("analyst@123"))
                .role(Role.ANALYST)
                .status(UserStatus.ACTIVE)
                .build());

        User admin = userRepository.save(User.builder()
                .username("admin.user")
                .password(passwordEncoder.encode("admin@123"))
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .build());

        LocalDate today = LocalDate.now();

        List<FinancialRecord> records = List.of(
                record("24000.00", FinancialRecordType.INCOME, "SaaS Revenue", today.minusDays(72), "SaaS Subscription Revenue - January", analyst),
                record("25500.00", FinancialRecordType.INCOME, "SaaS Revenue", today.minusDays(42), "SaaS Subscription Revenue - February", analyst),
                record("26800.00", FinancialRecordType.INCOME, "SaaS Revenue", today.minusDays(12), "SaaS Subscription Revenue - March", analyst),
                record("5000.00", FinancialRecordType.INCOME, "Consulting", today.minusDays(66), "Implementation Consulting Retainer", admin),
                record("3500.00", FinancialRecordType.INCOME, "Affiliate", today.minusDays(28), "Partner Referral Payout", viewer),

                record("4200.00", FinancialRecordType.EXPENSE, "Infrastructure", today.minusDays(78), "Q1 Server Costs", admin),
                record("980.00", FinancialRecordType.EXPENSE, "Tools", today.minusDays(75), "Monitoring Tool Subscription", analyst),
                record("1600.00", FinancialRecordType.EXPENSE, "Payroll", today.minusDays(70), "Contract QA Support", admin),
                record("820.00", FinancialRecordType.EXPENSE, "Operations", today.minusDays(62), "Domain and SSL Renewal", viewer),
                record("2100.00", FinancialRecordType.EXPENSE, "Marketing", today.minusDays(55), "Marketing Ad Spend - Search", analyst),
                record("1750.00", FinancialRecordType.EXPENSE, "Marketing", today.minusDays(48), "Marketing Ad Spend - Social", analyst),
                record("1200.00", FinancialRecordType.EXPENSE, "Infrastructure", today.minusDays(38), "Database Backup Storage", admin),
                record("890.00", FinancialRecordType.EXPENSE, "Tools", today.minusDays(30), "Design Collaboration Subscription", viewer),
                record("2300.00", FinancialRecordType.EXPENSE, "Operations", today.minusDays(24), "Customer Support Outsourcing", admin),
                record("950.00", FinancialRecordType.EXPENSE, "Security", today.minusDays(20), "Vulnerability Scan Service", admin),
                record("3100.00", FinancialRecordType.EXPENSE, "Infrastructure", today.minusDays(14), "Compute Burst Scaling", analyst),
                record("1320.00", FinancialRecordType.EXPENSE, "Compliance", today.minusDays(8), "Quarterly Compliance Review", admin),
                record("760.00", FinancialRecordType.EXPENSE, "Operations", today.minusDays(3), "Payment Gateway Charges", viewer)
        );

        financialRecordRepository.saveAll(records);
    }

    private FinancialRecord record(
            String amount,
            FinancialRecordType type,
            String category,
            LocalDate date,
            String notes,
            User createdBy
    ) {
        return FinancialRecord.builder()
                .amount(new BigDecimal(amount))
                .type(type)
                .category(category)
                .date(date)
                .notes(notes)
                .createdBy(createdBy)
                .isDeleted(false)
                .build();
    }
}
