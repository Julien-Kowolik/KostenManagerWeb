package de.julien.kostenmanagerweb.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "gruppe_id")
    private GruppeEntity gruppe;

    @OneToMany(mappedBy = "personAusgabe")
    private List<AusgabeEntity> ausgaben = new ArrayList<>();

    @OneToOne(mappedBy = "personBudget")
    private BudgetEntity budget;


    protected PersonEntity() {
    }

    public PersonEntity(String name, GruppeEntity gruppe) {
        this.name = name;
        this.gruppe = gruppe;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public GruppeEntity getGruppe() {
        return gruppe;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGruppe(GruppeEntity gruppe) {
        this.gruppe = gruppe;
    }
    public List<AusgabeEntity> getAusgaben() {
        return ausgaben;
    }

    public BudgetEntity getBudget() {
        return budget;
    }
    public void setBudget(BudgetEntity budget) {
        this.budget = budget;
    }
}