package de.julien.kostenmanagerweb.repository;
import de.julien.kostenmanagerweb.entity.BenutzerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BenutzerRepository extends JpaRepository<BenutzerEntity, Long> {

    BenutzerEntity findByUsername(String username);

    boolean existsByUsername(String username);
}
