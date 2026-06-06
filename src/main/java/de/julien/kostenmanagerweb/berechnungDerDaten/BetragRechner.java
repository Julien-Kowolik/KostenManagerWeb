package de.julien.kostenmanagerweb.berechnungDerDaten;

import de.julien.kostenmanagerweb.model.Ausgabe;
import de.julien.kostenmanagerweb.model.Schulden;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BetragRechner {
  // Grunddaten
  private final int gruppenGroesse;

  public BetragRechner(int gruppenGroesse) {
    this.gruppenGroesse = gruppenGroesse;
  }

  // Beträge berechnen
  public double gesamtBetrag(List<Ausgabe> ausgaben) {
    return runde(ausgaben.stream()
        .mapToDouble(Ausgabe::betrag)
        .sum());
  }

  public double durchschnittBetrag(List<Ausgabe> ausgaben) {
    return runde(gesamtBetrag(ausgaben) / gruppenGroesse);
  }

  public Map<String, Double> addedMap(List<Ausgabe> ausgaben) {
    return ausgaben.stream()
        .collect(Collectors.groupingBy(Ausgabe::name,
            Collectors.summingDouble(b -> runde(b.betrag()))));
  }

  private double runde(double wert) {
    return Math.round(wert * 100.0) / 100.0;
  }

  // Differenzen berechnen
  public double differenzBeiEinerPerson(String name, List<Ausgabe> ausgaben) {
    return runde(addedMap(ausgaben).get(name) - durchschnittBetrag(ausgaben));
  }

  public Map<String, Double> differenzVonAllen(List<Ausgabe> ausgaben) {
    Map<String, Double> differenzen = new HashMap<>();
    addedMap(ausgaben)
        .forEach((name, betrag) ->
            differenzen.put(
                name,
                runde(betrag - durchschnittBetrag(ausgaben)))
        );
    return differenzen;
  }

  // Schulden berechnen
  public List<Schulden> schuldenBerechnen(List<Ausgabe> ausgaben) {
    List<Schulden> schulden = new ArrayList<>();

    Map<String, Double> diffAlle = differenzVonAllen(ausgaben);
    List<PersonBetrag> personen = personBetragListe(diffAlle);

    List<PersonBetrag> schuldner = new ArrayList<>(getAllSchuldner(personen));
    List<PersonBetrag> empfaenger = new ArrayList<>(getAllEmpfaenger(personen));

    schuldenRechnung(schuldner, empfaenger, schulden);

    return schulden;
  }

  private List<PersonBetrag> getAllSchuldner(List<PersonBetrag> personen) {
    return personen.stream()
        .filter(person -> person.betrag() < 0)
        .toList();
  }

  private List<PersonBetrag> getAllEmpfaenger(List<PersonBetrag> personen) {
    return personen.stream()
        .filter(person -> person.betrag() > 0)
        .toList();
  }

  private List<PersonBetrag> personBetragListe(Map<String, Double> diffAlle) {
    return diffAlle.entrySet().stream()
        .map(entry -> new PersonBetrag(entry.getKey(), entry.getValue()))
        .toList();
  }

  private void schuldenRechnung(List<PersonBetrag> schuldner, List<PersonBetrag> empfaenger,
                                List<Schulden> schulden) {
    while (!schuldner.isEmpty() && !empfaenger.isEmpty()) {
      PersonBetrag s = schuldner.getFirst();
      PersonBetrag e = empfaenger.getFirst();

      double schuldet = Math.abs(s.betrag());
      double bekommt = e.betrag();
      //es muss die Kleinere zahl genommen werden da die größere summe nicht die zahlung sein kann
      double zahlung = runde(kleinereZahl(schuldet, bekommt));

      schulden.add(new Schulden(
          s.name(),
          e.name(),
          zahlung
      ));

      if (schuldet - zahlung == 0) {
        schuldner.removeFirst();
      } else {
        schuldner.set(0, new PersonBetrag(s.name(), runde(schuldet - zahlung)));
      }

      if (bekommt - zahlung == 0) {
        empfaenger.removeFirst();
      } else {
        empfaenger.set(0, new PersonBetrag(e.name(), runde(bekommt - zahlung)));
      }
    }
  }

  private double kleinereZahl(double schuldet, double bekommt) {
    if (schuldet > bekommt) {
      return bekommt;
    } else {
      return schuldet;
    }
  }

  // Hilfsmodell für interne Berechnungen
  private record PersonBetrag(String name, double betrag) {
  }
}
