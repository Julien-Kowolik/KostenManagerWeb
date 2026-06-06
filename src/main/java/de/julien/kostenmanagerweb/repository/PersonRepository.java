package de.julien.kostenmanagerweb.repository;

import de.julien.kostenmanagerweb.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
}
