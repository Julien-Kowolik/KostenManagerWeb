package de.julien.kostenmanagerweb.entity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class BenutzerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String passwordHash;

    @OneToMany(mappedBy = "benutzer")
    private List<GruppeEntity> gruppen = new ArrayList<>();

    protected BenutzerEntity() {}

    public BenutzerEntity(String username, String password_hash) {
        this.username = username;
        this.passwordHash = password_hash;
    }
    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword_hash() {
        return passwordHash;
    }
    public List<GruppeEntity> getGruppen() {
        return gruppen;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword_hash(String password_hash) {
        this.passwordHash = password_hash;
    }
}
