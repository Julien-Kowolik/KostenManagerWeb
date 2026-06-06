package de.julien.kostenmanagerweb.kundenKommunikation;

import de.julien.kostenmanagerweb.model.Ausgabe;
import de.julien.kostenmanagerweb.model.BudgetAnalyseEintrag;
import de.julien.kostenmanagerweb.model.Schulden;
import java.util.List;
import java.util.Map;

public class Printer {


  public void personNichtGefunden(String name) {
    System.out.println(name + " ist nicht in der Liste");
  }

  public void diffAlle(Map<String, Double> diffAlle) {
    System.out.printf(
        "%-10s | %10s%n",
        "Name",
        "Differenz"
    );

    System.out.println("--------------------------");

    diffAlle.forEach((name, differenz) -> {
      System.out.printf(
          "%-10s | %10.2f %n",
          name,
          differenz
      );
    });
  }

  public void schuldenInTabellenformat(List<Schulden> schulden) {
    System.out.printf(
        "%-12s | %-12s | %10s%n",
        "Von",
        "An",
        "Betrag"
    );

    System.out.println(
        "------------------------------------------"
    );

    schulden.forEach(schuld ->
        System.out.printf(
            "%-12s | %-12s | %10.2f %n",
            schuld.von(),
            schuld.an(),
            schuld.betrag()
        )
    );
  }

  public void gesamteAusgaben(double gesamteAusgaben, List<Ausgabe> ausgaben) {
    System.out.printf(
        "%-10s | %-15s | %10s%n",
        "Name",
        "Ausgabe",
        "Betrag"
    );

    System.out.println("-------------------------------------------");

    ausgaben.forEach(ausgabe ->
        System.out.printf(
            "%-10s | %-15s | %10.2f %n",
            ausgabe.name(),
            ausgabe.ausgabeArt(),
            ausgabe.betrag()
        )
    );
    System.out.println("------------------------------------------\n" +
        "Es wurde insgedamt: " + gesamteAusgaben + "€ ausgeben");
  }

  public void budgetWurdeUeberschritten(String name, double budgetUeberschreitung,
                                        double budget) {
    System.out.println(
        name
            + " hat den gesetzten Budget von "
            + budget
            + " mit "
            + budgetUeberschreitung
            + " überschritten");
  }

  public void keinBudgetFestgelegt(String name) {
    System.out.println(name + " hat kein Budget festgelegt");
  }

  public void budgetInTabellenformat(List<BudgetAnalyseEintrag> analysen) {
    System.out.println(
        "Name           Budget    Ausgegeben    Rest      Status");
    System.out.println(
        "------------------------------------------------------");

    for (BudgetAnalyseEintrag eintrag : analysen) {

      String status =
          eintrag.ueberschritten()
              ? "ÜBERSCHRITTEN"
              : "OK";

      System.out.printf(
          "%-12s %8.2f  %11.2f  %9.2f    %s%n",
          eintrag.name(),
          eintrag.budget(),
          eintrag.ausgegeben(),
          eintrag.restBudget(),
          status
      );
    }
  }

  public void ausgabenEinerPerson(List<Ausgabe> ausgaben, double gesamt) {

    System.out.println();
    System.out.println("===== Ausgaben von " + ausgaben.getFirst().name() + " =====");
    System.out.println();

    System.out.println("Betrag      | Beschreibung");
    System.out.println("----------------------------");

    for (Ausgabe ausgabe : ausgaben) {
      System.out.printf(
          "%10.2f | %s%n",
          ausgabe.betrag(),
          ausgabe.ausgabeArt()
      );
    }

    System.out.println("----------------------------");

    System.out.printf(
        "Gesamt: %.2f €%n",
        gesamt
    );

    System.out.println();
  }

  public void differenzVon(String name, double diff) {
    System.out.println(name + " hat eine differenz von " + diff);
  }

  public void budgetVon(String name, double budget) {
    System.out.println(name + " hat ein budget von " + budget);
  }

  public void hatKeineSchulden(String name) {
    System.out.println(name + " hat keine Schulden");
  }

  public void schuldenVon(List<Schulden> schuldenVon) {

      System.out.println();
      System.out.println("===== Schulden von " + schuldenVon.getFirst().von() + " =====");
      System.out.println();

      System.out.println("An           | Betrag");
      System.out.println("-----------------------");

      for (Schulden schuld : schuldenVon) {
        System.out.printf(
            "%-12s | %8.2f%n",
            schuld.an(),
            schuld.betrag()
        );
      }

      System.out.println();
    }

  public void personenDetails(
      String name,
      double budget,
      double restBudget,
      double differenz,
      List<Ausgabe> ausgaben,
      List<Schulden> schulden) {
    System.out.println();
    System.out.println("===== Personendetails =====");
    System.out.println();

    System.out.println("Name: " + name);
    System.out.printf("Budget: %.2f €%n", budget);
    System.out.printf("Restbudget: %.2f €%n", restBudget);
    System.out.printf("Differenz: %.2f €%n", differenz);

    System.out.println();

    System.out.println("Ausgaben");
    System.out.println("--------------------------------");

    for (Ausgabe ausgabe : ausgaben) {
      System.out.printf(
          "%10.2f € | %s%n",
          ausgabe.betrag(),
          ausgabe.ausgabeArt()
      );
    }

    System.out.println();

    System.out.println("Schulden");
    System.out.println("--------------------------------");

    if (schulden.isEmpty()) {
      System.out.println("Keine Schulden vorhanden.");
    } else {
      for (Schulden schuld : schulden) {
        System.out.printf(
            "An %-12s : %8.2f €%n",
            schuld.an(),
            schuld.betrag()
        );
      }
    }

    System.out.println();
  }
}

