package tech.maxanderson.calculator.frontend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.maxanderson.calculator.frontend.entity.CalculationLog;

@Repository
public interface CalculationLogRepository extends JpaRepository<CalculationLog, Long> {
}
