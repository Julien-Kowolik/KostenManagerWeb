package de.julien.kostenmanagerweb.repository;
import de.julien.kostenmanagerweb.entity.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<BudgetEntity, Long> {
}
