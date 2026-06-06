package de.julien.kostenmanagerweb.entity;
import jakarta.persistence.*;

@Entity
public class AusgabeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double betrag;
    private String bezeichnung;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private PersonEntity personAusgabe;

    protected AusgabeEntity() {
    }

    public AusgabeEntity(PersonEntity person, double betrag, String bezeichnung) {
        this.personAusgabe = person;
        this.betrag = betrag;
        this.bezeichnung = bezeichnung;
    }

    public double getBetrag() {
        return betrag;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public PersonEntity getPerson() {
        return personAusgabe;
    }

    public void setBetrag(double betrag) {
        this.betrag = betrag;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public void setPerson(PersonEntity person) {
        this.personAusgabe = person;
    }
}
