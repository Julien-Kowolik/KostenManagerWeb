package de.julien.kostenmanagerweb.repository;
import de.julien.kostenmanagerweb.entity.SchuldenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchuldenRepository extends JpaRepository<SchuldenEntity, Long> {
}
