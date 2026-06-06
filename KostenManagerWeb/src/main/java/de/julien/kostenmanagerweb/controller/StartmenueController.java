package de.julien.kostenmanagerweb.controller;

import de.julien.kostenmanagerweb.entity.BenutzerEntity;
import de.julien.kostenmanagerweb.entity.GruppeEntity;
import de.julien.kostenmanagerweb.entity.PersonEntity;
import de.julien.kostenmanagerweb.repository.BenutzerRepository;
import de.julien.kostenmanagerweb.repository.GruppeRepository;
import de.julien.kostenmanagerweb.repository.PersonRepository;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class StartmenueController {
    private final PersonRepository personRepository;
    private final BenutzerRepository benutzerRepository;
    private final GruppeRepository gruppeRepository;

    public StartmenueController(
            BenutzerRepository benutzerRepository,
            GruppeRepository gruppeRepository,
            PersonRepository personRepository
    ) {
        this.benutzerRepository = benutzerRepository;
        this.gruppeRepository = gruppeRepository;
        this.personRepository = personRepository;
    }

    @GetMapping("/startmenue")
    public String startmenue(HttpSession session, Model model) {

        Long benutzerId = (Long) session.getAttribute("benutzerId");

        if (benutzerId == null) {
            return "redirect:/login";
        }

        BenutzerEntity benutzer = benutzerRepository.findById(benutzerId).orElse(null);

        if (benutzer == null) {
            return "redirect:/login";
        }

        List<GruppeEntity> gruppen = gruppeRepository.findByBenutzer_Id(benutzerId);

        model.addAttribute("username", benutzer.getUsername());
        model.addAttribute("gruppen", gruppen);

        return "startmenue";
    }

    @PostMapping("/startmenue/gruppe-erstellen")
    public String gruppeErstellen(String gruppenName, String personenNamen, HttpSession session) {

        Long benutzerId = (Long) session.getAttribute("benutzerId");

        if (benutzerId == null) {
            return "redirect:/login";
        }

        if (gruppenName == null || gruppenName.isBlank()) {
            return "redirect:/startmenue";
        }

        BenutzerEntity benutzer = benutzerRepository.findById(benutzerId).orElse(null);

        if (benutzer == null) {
            return "redirect:/login";
        }

        GruppeEntity neueGruppe = new GruppeEntity(gruppenName.trim(), benutzer);
        gruppeRepository.save(neueGruppe);

        if (personenNamen != null && !personenNamen.isBlank()) {
            String[] namen = personenNamen.split(",");

            for (String name : namen) {
                String saubererName = name.trim();

                if (!saubererName.isBlank()) {
                    personRepository.save(
                            new PersonEntity(saubererName, neueGruppe)
                    );
                }
            }
        }

        return "redirect:/startmenue";
    }

    @PostMapping("/gruppe-loeschen")
    public String gruppeLoeschen(Long gruppenId,
                                 HttpSession session) {

        Long benutzerId =
                (Long) session.getAttribute("benutzerId");

        if (benutzerId == null) {
            return "redirect:/login";
        }

        GruppeEntity gruppe =
                gruppeRepository.findById(gruppenId)
                        .orElse(null);

        if (gruppe == null) {
            return "redirect:/startmenue";
        }

        if (!gruppe.getBenutzer().getId().equals(benutzerId)) {
            return "redirect:/startmenue";
        }

        gruppeRepository.delete(gruppe);

        return "redirect:/startmenue";
    }

    @GetMapping("/gruppe/{gruppenId}/oeffnen")
    public String gruppeOeffnen(@PathVariable Long gruppenId, HttpSession session) {

        Long benutzerId = (Long) session.getAttribute("benutzerId");

        if (benutzerId == null) {
            return "redirect:/login";
        }

        GruppeEntity gruppe = gruppeRepository.findById(gruppenId).orElse(null);

        if (gruppe == null || gruppe.getBenutzer() == null) {
            return "redirect:/startmenue";
        }

        if (!gruppe.getBenutzer().getId().equals(benutzerId)) {
            return "redirect:/startmenue";
        }

        session.setAttribute("gruppenId", gruppenId);

        return "redirect:/dashboard";
    }
}