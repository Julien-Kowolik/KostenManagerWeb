package de.julien.kostenmanagerweb.entity;
import jakarta.persistence.*;

@Entity
public class BudgetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double betrag;

    @OneToOne
    @JoinColumn(name = "person_id")
    private PersonEntity personBudget;

    protected BudgetEntity() {}

    public BudgetEntity(double betrag, PersonEntity person) {
        this.betrag = betrag;
        this.personBudget = person;
    }

    public double getBetrag() {
        return betrag;
    }

    public void setBetrag(double betrag) {
        this.betrag = betrag;
    }

    public PersonEntity getPerson() {
        return personBudget;
    }
    public void setPerson(PersonEntity person) {
        this.personBudget = person;
    }
    public Long getId() {
        return id;
    }


}
