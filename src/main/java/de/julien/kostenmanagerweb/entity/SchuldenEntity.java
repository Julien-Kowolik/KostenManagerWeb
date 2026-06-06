package de.julien.kostenmanagerweb.entity;
import jakarta.persistence.*;

@Entity
public class SchuldenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double betrag;

    @ManyToOne
    @JoinColumn(name = "schuldner_id")
    private PersonEntity schuldner;

    @ManyToOne
    @JoinColumn(name = "glaubiger_id")
    private PersonEntity glaubiger;

    protected SchuldenEntity() {}

    public SchuldenEntity(PersonEntity schuldner, PersonEntity glaubiger, double betrag) {
        this.schuldner = schuldner;
        this.glaubiger = glaubiger;
        this.betrag = betrag;
    }
    public Long getId() {
        return id;
    }
    public double getBetrag() {
        return betrag;
    }

    public PersonEntity getSchuldner() {
        return schuldner;
    }
    public PersonEntity getGlaubiger() {
        return glaubiger;
    }
    public void setBetrag(double betrag) {
        this.betrag = betrag;
    }
    public void setSchuldner(PersonEntity schuldner) {
        this.schuldner = schuldner;
    }
    public void setGlaubiger(PersonEntity glaubiger) {
        this.glaubiger = glaubiger;
    }

}
