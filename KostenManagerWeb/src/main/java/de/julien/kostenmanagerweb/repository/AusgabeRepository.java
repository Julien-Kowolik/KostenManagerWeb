package de.julien.kostenmanagerweb.repository;
import de.julien.kostenmanagerweb.entity.AusgabeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AusgabeRepository extends JpaRepository<AusgabeEntity, Long> {
}
