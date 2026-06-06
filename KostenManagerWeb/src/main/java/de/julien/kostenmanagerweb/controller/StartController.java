package de.julien.kostenmanagerweb.controller;

import de.julien.kostenmanagerweb.entity.GruppeEntity;
import de.julien.kostenmanagerweb.entity.PersonEntity;
import de.julien.kostenmanagerweb.model.Ausgabe;
import de.julien.kostenmanagerweb.repository.GruppeRepository;
import de.julien.kostenmanagerweb.repository.PersonRepository;
import de.julien.kostenmanagerweb.service.KostenManager;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class StartController {

  private final GruppeRepository gruppeRepository;
  private final PersonRepository personRepository;
  private final Map<Long, KostenManager> gruppenManager = new HashMap<>();

  public StartController(GruppeRepository gruppeRepository,
                         PersonRepository personRepository) {
    this.gruppeRepository = gruppeRepository;
    this.personRepository = personRepository;
  }

  @GetMapping("/")
  public String startseite() {
    return "redirect:/login";
  }

  private Long getGruppenId(HttpSession session) {
    return (Long) session.getAttribute("gruppenId");
  }

  private KostenManager getKostenManager(Long gruppenId) {
    return gruppenManager.computeIfAbsent(gruppenId, id -> {
      KostenManager manager = new KostenManager();

      GruppeEntity gruppe = gruppeRepository.findById(id).orElse(null);

      if (gruppe != null) {
        for (PersonEntity person : gruppe.getPersonen()) {
          manager.addPerson(person.getName());
        }
      }

      return manager;
    });
  }

  @PostMapping("/person-hinzufuegen")
  public String personHinzufuegen(String name, HttpSession session) {
    Long gruppenId = getGruppenId(session);

    if (gruppenId == null) {
      return "redirect:/startmenue";
    }

    if (name == null || name.isBlank()) {
      return "redirect:/personen";
    }

    GruppeEntity gruppe = gruppeRepository.findById(gruppenId).orElse(null);

    if (gruppe == null) {
      return "redirect:/startmenue";
    }

    KostenManager manager = getKostenManager(gruppenId);
    String saubererName = name.trim();

    boolean existiertSchon = manager.getPersonen().stream()
            .anyMatch(p -> p.equalsIgnoreCase(saubererName));

    if (!existiertSchon) {
      personRepository.save(new PersonEntity(saubererName, gruppe));
      manager.addPerson(saubererName);
    }

    return "redirect:/personen";
  }

  @GetMapping("/dashboard")
  public String dashboard(Model model, HttpSession session) {
    Long gruppenId = getGruppenId(session);

    if (gruppenId == null) {
      return "redirect:/startmenue";
    }

    KostenManager gruppe = getKostenManager(gruppenId);

    model.addAttribute("budgetAnalyse", gruppe.getBudgetAnalyse());
    model.addAttribute("personen", gruppe.getPersonen());
    model.addAttribute("personenAnzahl", gruppe.getPersonen().size());
    model.addAttribute("gesamtBetrag", gruppe.getGesamtBetrag());
    model.addAttribute("gruppenBudget", gruppe.getGruppenBudget());
    model.addAttribute("gruppenRestBudget", gruppe.getGruppenRestBudget());

    Ausgabe hoechsteAusgabe = gruppe.getHoechsteAusgabe();

    model.addAttribute(
            "hoechsteAusgabe",
            hoechsteAusgabe == null
                    ? "Keine Ausgaben"
                    : hoechsteAusgabe.name() + " (" + hoechsteAusgabe.betrag() + " €)"
    );

    model.addAttribute("groessterSchuldner", gruppe.getGroessterSchuldner());
    model.addAttribute("groessterGlaeubiger", gruppe.getGroessterGlaeubiger());

    return "dashboard";
  }

  @GetMapping("/budgets")
  public String budgets(Model model, HttpSession session) {
    Long gruppenId = getGruppenId(session);

    if (gruppenId == null) {
      return "redirect:/startmenue";
    }

    KostenManager gruppe = getKostenManager(gruppenId);

    model.addAttribute("personen", gruppe.getPersonen());
    model.addAttribute("budgetAnalyse", gruppe.getBudgetAnalyse());

    return "budgets";
  }

  @PostMapping("/budget-setzen")
  public String budgetSetzen(String name, double budget, HttpSession session) {
    Long gruppenId = getGruppenId(session);

    if (gruppenId == null) {
      return "redirect:/startmenue";
    }

    KostenManager gruppe = getKostenManager(gruppenId);

    if (name == null || name.isBlank()) {
      return "redirect:/budgets";
    }

    if (budget < 0) {
      return "redirect:/budgets";
    }

    String saubererName = name.trim();

    boolean personExistiert = gruppe.getPersonen().stream()
            .anyMatch(p -> p.equalsIgnoreCase(saubererName));

    if (!personExistiert) {
      return "redirect:/budgets";
    }

    gruppe.setBudget(saubererName, budget);

    return "redirect:/budgets";
  }

  @GetMapping("/ausgaben")
  public String ausgaben(Model model, HttpSession session) {
    Long gruppenId = getGruppenId(session);

    if (gruppenId == null) {
      return "redirect:/startmenue";
    }

    KostenManager gruppe = getKostenManager(gruppenId);

    model.addAttribute("ausgaben", gruppe.getAusgaben());
    model.addAttribute("personen", gruppe.getPersonen());

    return "ausgaben";
  }

  @PostMapping("/ausgaben-hinzufuegen")
  public String ausgabenHinzufuegen(String name,
                                    double betrag,
                                    String ausgabe,
                                    HttpSession session) {
    Long gruppenId = getGruppenId(session);

    if (gruppenId == null) {
      return "redirect:/startmenue";
    }

    KostenManager gruppe = getKostenManager(gruppenId);

    if (name == null || name.isBlank()) {
      return "redirect:/ausgaben";
    }

    if (betrag <= 0) {
      return "redirect:/ausgaben";
    }

    if (ausgabe == null || ausgabe.isBlank()) {
      return "redirect:/ausgaben";
    }

    gruppe.addAusgabe(
            name.trim(),
            betrag,
            ausgabe.trim()
    );

    return "redirect:/ausgaben";
  }

  @GetMapping("/schulden")
  public String schulden(Model model, HttpSession session) {
    Long gruppenId = getGruppenId(session);

    if (gruppenId == null) {
      return "redirect:/startmenue";
    }

    KostenManager gruppe = getKostenManager(gruppenId);

    model.addAttribute("personen", gruppe.getPersonen());
    model.addAttribute("schulden", gruppe.getSchulden());

    return "schulden";
  }

  @PostMapping("/schulden-ausgleichen")
  public String schuldenAusgleichen(String von,
                                    String an,
                                    double betrag,
                                    HttpSession session) {
    Long gruppenId = getGruppenId(session);

    if (gruppenId == null) {
      return "redirect:/startmenue";
    }

    KostenManager gruppe = getKostenManager(gruppenId);

    if (von == null || von.isBlank()) {
      return "redirect:/schulden";
    }

    if (betrag <= 0) {
      return "redirect:/schulden";
    }

    if (an == null || an.isBlank()) {
      return "redirect:/schulden";
    }

    gruppe.bezahlteSchulden(von, an, betrag);

    return "redirect:/schulden";
  }

  @GetMapping("/personen")
  public String personen(Model model, String name, HttpSession session) {
    Long gruppenId = getGruppenId(session);

    if (gruppenId == null) {
      return "redirect:/startmenue";
    }

    KostenManager gruppe = getKostenManager(gruppenId);

    model.addAttribute("personen", gruppe.getPersonen());
    model.addAttribute("ausgewaehltePerson", name);

    if (name != null && !name.isBlank()) {
      model.addAttribute("ausgabenPerson", gruppe.getAusgabenVon(name));
      model.addAttribute("gesamtAusgabenPerson", gruppe.getSumAusgabenVon(name));
      model.addAttribute("budgetPerson", gruppe.getBudget(name));
      model.addAttribute("restBudgetPerson", gruppe.getRestBudget(name));
      model.addAttribute("differenzPerson", gruppe.getDifferenzPerson(name));
      model.addAttribute("schuldenPerson", gruppe.getSchuldenVonPerson(name));
      model.addAttribute("ausgabenAnzahlPerson", gruppe.getAusgabenVon(name).size());
    }

    return "personen";
  }
}