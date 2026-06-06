package de.julien.kostenmanagerweb.service;

import de.julien.kostenmanagerweb.berechnungDerDaten.BetragRechner;
import de.julien.kostenmanagerweb.dateiLeser.CsvAusgabenLeser;

import java.util.*;

import de.julien.kostenmanagerweb.kundenKommunikation.Printer;
import de.julien.kostenmanagerweb.model.Ausgabe;
import de.julien.kostenmanagerweb.model.BudgetAnalyseEintrag;
import de.julien.kostenmanagerweb.model.Schulden;




public class KostenManager {
  // Grunddaten und Services
  private final List<Ausgabe> ausgaben = new ArrayList<>(); //Speichert von der Gruppe Getätigkten ausgaben
  private BetragRechner rechner;
  private final Printer print = new Printer();
  private final List<Schulden> schulden = new ArrayList<>();//Speichert alle verbleibenden Schulden
  private final List<Ausgabe> schuldAusgleich = new ArrayList<>();  //Speichert alle schuldbegleichungen15
  private final BudgetManager budgetManager = new BudgetManager();
  private final List<String> personen = new ArrayList<>();


  // Konstruktoren
  public KostenManager(List<String> namen) {
    if (namen.isEmpty()) {
      throw new IllegalArgumentException(
          "Eine Gruppe muss mindestens eine Person enthalten."
      );
    }
    this.personen.addAll(namen);
    this.rechner = new BetragRechner(personen.size());
  }

  public KostenManager(String[] namen) {
    if (namen.length < 1) {
      throw new IllegalArgumentException(
          "Eine Gruppe muss mindestens eine Person enthalten."
      );
    }
    this.personen.addAll(Arrays.stream(namen).toList());
    this.rechner = new BetragRechner(personen.size());
  }

  public KostenManager() {
    this.rechner = new BetragRechner(personen.size());
  }

  public void addPerson(String name) {
    if(!istDabei(name) && !name.isBlank()) {
      this.personen.add(name);
      this.rechner = new BetragRechner(personen.size());
    }
  }

  // Ausgaben verwalten

  private List<Ausgabe> getAusgabenZumRechnen() {
    List<Ausgabe> ausgabenKopie = new ArrayList<>(ausgaben);

    for(String person: personen) {
      boolean hatAusgabe = ausgabenKopie.stream()
          .anyMatch(a -> a.name().equalsIgnoreCase(person));
      if(!hatAusgabe) {
        ausgabenKopie.add(new Ausgabe(person, 0, "Keine Ausgaben"));
      }
    }
    return ausgabenKopie;
  }

  public Ausgabe getAusgabe(int index) {
    if (index >= ausgaben.size()) {
      System.out.println("Ausgabe nicht gefunden!");
      return null;
    }
    return ausgaben.get(index);
  }

  public Ausgabe getHoechsteAusgabe() {
    Ausgabe hoechsteAusgabe = ausgaben.stream()
        .max(Comparator.comparingDouble(Ausgabe::betrag))
        .orElse(null);
    return hoechsteAusgabe;
  }

  public String getGroessterSchuldner() {
    schuldenNeuBerechnen();
    return getDiffAlle().entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(e -> e.getKey() + " (" + Math.abs(e.getValue()) + " €)")
            .orElse("Keine Daten");
  }

  public String getGroessterGlaeubiger() {
    schuldenNeuBerechnen();
    return getDiffAlle().entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(e -> e.getKey() + " (" + e.getValue() + " €)")
            .orElse("Keine Daten");
  }

  public int getAusgabenSize() {
    return ausgaben.size();
  }

  public void dateiEinlesen(String dateiName) {
    List<Ausgabe> ausgabenKopie = CsvAusgabenLeser.leseDatei(dateiName, personen);
    ausgaben.addAll(ausgabenKopie);
    ausgabenKopie.stream()
        .map(Ausgabe::name)
        .distinct()
        .forEach(this::budgetPruefen);
    schuldenNeuBerechnen();
  }

  public void addAusgabe(String name, double betrag, String art) {
    if (istDabei(name)) {
      ausgaben.add(new Ausgabe(name, betrag, art));
      schuldenNeuBerechnen();
      budgetPruefen(name);
      return;
    }
    print.personNichtGefunden(name);
  }

  public void printGesamteAusgaben() {
    print.gesamteAusgaben(getGesamtBetrag(), ausgaben);
  }

  public List<Ausgabe> getAusgaben() {
    return List.copyOf(ausgaben);
  }

  public void printGesamteAusgabenVon(String name) {
    List<Ausgabe> ausgaben = getGesamteAusgabenVon(name);
    if (istDabei(name)) {
      double gesamteAusaben = getSumAusgabenVon(name);
      if (ausgaben.isEmpty()) {
        System.out.println(name + " hat keine Ausgaben.");
        return;
      }
      print.ausgabenEinerPerson(ausgaben, gesamteAusaben);
    }
  }

  public List<Ausgabe> getGesamteAusgabenVon(String name) {
    return ausgaben.stream()
        .filter(a -> a.name().equals(name))
        .toList();
  }

  private Map<String, Double> getGesamtAusgaben() {
    return rechner.addedMap(getAusgabenZumRechnen());
  }

  // Gesamtwerte und Differenzen
  public double getGesamtBetrag() {
    return rechner.gesamtBetrag(getAusgabenZumRechnen());
  }

  public double getDurchschnittBetrag() {
    return rechner.durchschnittBetrag(getAusgabenZumRechnen());
  }

  public double getDifferenzPerson(String name) {
    if (!istDabei(name)) {
      return 0;
    }
    return rechner.differenzBeiEinerPerson(name, getAusgabenZumRechnen());
  }

  public Map<String, Double> getDiffAlle() {
    return rechner.differenzVonAllen(getAusgabenZumRechnen());
  }

  public double getSumAusgabenVon(String name) {
    if (istDabei(name)) {
      return getGesamtAusgaben().getOrDefault(name, 0.0);
    }
    return 0;
  }

  public void printDifferenzVon(String name) {
    if(istDabei(name)) {
      double diff = getDifferenzPerson(name);
      print.differenzVon(name, diff);
    }
  }

  public void printDiffAlle() {
    print.diffAlle(getDiffAlle());
  }

  // Schulden berechnen und ausgeben
  public List<Schulden> getSchulden() {
    schuldenNeuBerechnen();
    return List.copyOf(schulden);
  }

  public void printSchuldenAnalyse() {
    print.schuldenInTabellenformat(schulden);
  }

  public void bezahlteSchulden(String von, String an, double betrag) {
    schuldAusgleich.add(new Ausgabe(von, betrag, "Zahlung an " + an));
    schuldAusgleich.add(new Ausgabe(an, -betrag, "Zahlung von " + von));
    wendeZahlungAn(von, an, betrag);
  }

  public void printSchuldenVon(String name) {
    if(istDabei(name) ) {
      List<Schulden> schuldenVon = getSchuldenVon(name);
      if(schuldenVon.isEmpty()) {
        print.hatKeineSchulden(name);
      }else{
        print.schuldenVon(schuldenVon);
      }
    }

  }

  private void schuldenNeuBerechnen() {
    //nach jedem neuen eintag wird schuldausgleich mit dem eintrag angepasst und
    //eine neue schulden rechnung wird mit allen ausgaben + bezhalungen von Schulden berechnet
    List<Ausgabe> alleAusgaben = new ArrayList<>(getAusgabenZumRechnen());
    alleAusgaben.addAll(schuldAusgleich);

    schulden.clear();
    schulden.addAll(rechner.schuldenBerechnen(alleAusgaben));
  }

  private List<Schulden> getSchuldenVon(String name) {
    return schulden.stream()
        .filter(n -> n.von().equals(name))
        .toList();
  }

  private void wendeZahlungAn(String von, String an, double betrag) {
    int index = 0;
    for (Schulden s : schulden) {
      if (s.von().equals(von) && s.an().equals(an)) {
        double rest = s.betrag() - betrag;
        if (rest > 0.01) {
          schulden.set(index, new Schulden(von, an, rest));
        } else if (rest < 0.01 && rest > -0.01) {
          schulden.remove(index);
        } else {
          schulden.remove(index);
          schulden.add(new Schulden(an, von, Math.abs(rest)));
        }
        return;
      }
      index++;
    }
  }

  // Budgets verwalten und analysieren
  public void setBudget(String name, double budget) {
    if (istDabei(name)) {
      budgetManager.setBudget(name, budget);
    }
  }

  public double getGruppenBudget() {
    return budgetManager.getGruppenBudget();
  }

  public double getGruppenRestBudget() {
    return budgetManager.getGruppenRestBudget(getGesamtAusgaben());
  }

  public double getBudget(String name) {
    if (istDabei(name) && hatBudget(name)) {
      return budgetManager.getBudget(name);
    }
    return 0;
  }

  public double getRestBudget(String name) {
    if (istDabei(name) && hatBudget(name)) {
      return budgetManager.getRestBudget(name, getSumAusgabenVon(name));
    }
    return 0;
  }

  public void printBudgetVon(String name){
    if (istDabei(name) && hatBudget(name)) {
      double budget = budgetManager.getBudget(name);
      print.budgetVon(name, budget);
    }else{
      print.keinBudgetFestgelegt(name);
    }

  }

  public void printBudgetAnalyse() {
    List<BudgetAnalyseEintrag> analysierteListe = budgetAnalysieren();
    print.budgetInTabellenformat(analysierteListe);
  }

  private boolean hatBudget(String name) {
    boolean hatBudget =
        budgetManager.hatBudget(name);
    if (!hatBudget) {
      print.keinBudgetFestgelegt(name);
    }
    return hatBudget;
  }

  private void budgetPruefen(String name) {
    double ausgegeben = getSumAusgabenVon(name);
    if (budgetManager.istBudgetUeberschritten(name, ausgegeben)) {
      double budget = getBudget(name);
      double budgetUeberschritten = budgetManager.getBudgetUeberschreitung(name, getSumAusgabenVon(name));
      print.budgetWurdeUeberschritten(
          name,
          budgetUeberschritten,
          budget);
    }
  }

  private List<BudgetAnalyseEintrag> budgetAnalysieren() {
    List<BudgetAnalyseEintrag> analysierteListe = new ArrayList<>();
    for(String person: personen) {
      if(!budgetManager.hatBudget(person)) {
        continue;
      }
      double budget = getBudget(person);
      double ausgegeben = getSumAusgabenVon(person);
      double restBudget = getRestBudget(person);
      boolean ueberschritten = budgetManager.istBudgetUeberschritten(person, ausgegeben);

      analysierteListe.add(new BudgetAnalyseEintrag(
          person,
          budget,
          ausgegeben,
          restBudget,
          ueberschritten
      ));
    }
    return analysierteListe;
  }

  // Personen und Detailansicht
  public List<String> getPersonen() {
    return List.copyOf(personen);
  }

  public void printPersonenDetails(String name) {
    if(istDabei(name) ) {
      double budget = getBudget(name);
      double restBudget = getRestBudget(name);
      double differenzPerson = getDifferenzPerson(name);
      List<Ausgabe> gesamteAusgabenVon = getGesamteAusgabenVon(name);
      List<Schulden> schuldenVon = getSchuldenVon(name);

      print.personenDetails(
          name,
          budget,
          restBudget,
          differenzPerson,
          gesamteAusgabenVon,
          schuldenVon
          );
    }

  }

  private boolean istDabei(String name) {
    boolean istDabei = personen.stream()
        .map(String::toUpperCase)
        .anyMatch(n -> n.equals(name.toUpperCase()));
    if (!istDabei) {
      print.personNichtGefunden(name);
    }
    return istDabei;
  }

  //Datenzugriff für andere Oberflächen
  public List<BudgetAnalyseEintrag> getBudgetAnalyse() {
    return List.copyOf(budgetAnalysieren());
  }

  public List<Ausgabe> getAusgabenVon(String name) {
    if (istDabei(name)) {
      return List.copyOf(getGesamteAusgabenVon(name));
    }
    return List.of();
  }

  public List<Schulden> getSchuldenVonPerson(String name) {
    if (istDabei(name)) {
      return List.copyOf(getSchuldenVon(name));
    }
    return List.of();
  }

  public Map<String, Double> getAlleDifferenzen() {
    return Map.copyOf(getDiffAlle());
  }

}
