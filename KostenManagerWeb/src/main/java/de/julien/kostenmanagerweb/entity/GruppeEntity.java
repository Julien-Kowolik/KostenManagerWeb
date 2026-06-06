package de.julien.kostenmanagerweb.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class GruppeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gruppenName;

    @OneToMany(mappedBy = "gruppe")
    private List<PersonEntity> personen = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "benutzer_id")
    private BenutzerEntity benutzer;

    protected GruppeEntity() {
    }

    public GruppeEntity(String gruppenName, BenutzerEntity benutzer) {
        this.gruppenName = gruppenName;
        this.benutzer = benutzer;
    }
    public BenutzerEntity getBenutzer() {
        return benutzer;
    }
    public Long getId() {
        return id;
    }

    public String getGruppenName() {
        return gruppenName;
    }

    public void setGruppenName(String gruppenName) {
        this.gruppenName = gruppenName;
    }

    public List<PersonEntity> getPersonen() {
        return personen;
    }
}