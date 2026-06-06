package de.julien.kostenmanagerweb.repository;

import de.julien.kostenmanagerweb.entity.GruppeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GruppeRepository extends JpaRepository<GruppeEntity, Long> {
    List<GruppeEntity> findByBenutzer_Id(Long benutzerId);
}