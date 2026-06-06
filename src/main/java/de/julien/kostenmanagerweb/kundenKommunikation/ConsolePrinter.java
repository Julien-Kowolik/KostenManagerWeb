package de.julien.kostenmanagerweb.kundenKommunikation;

import java.util.List;

public class ConsolePrinter {

  // Start- und Hauptansichten
  public void begruessung() {
    System.out.println("""
        
        ========================
              SPLITTER
        ========================
        """);
  }

  public void gruppenErstellung() {
    System.out.println("""
        Bitte gib alle Teilnehmer ein.
        Namen mit Komma trennen.
        
        Beispiel:
        Willy,Tim,Gaby
        """);
  }

  public void hauptmenue() {
    System.out.println("""
        
        ===== Hauptmenü =====
        
        1 - Ausgaben
        2 - Schulden
        3 - Budgets
        4 - Personen
        0 - Beenden
        
        Auswahl:
        """);
  }

  // Untermenüs
  public void ausgabenMenue() {
    System.out.println("""
        
        ===== Ausgaben =====
        
        1 - CSV-Datei laden
        2 - Einzelne Ausgabe hinzufügen
        3 - Alle Ausgaben anzeigen
        0 - Zurück
        
        Auswahl:
        """);
  }

  public void schuldenMenue() {
    System.out.println("""
        
        ===== Schulden =====
        
        1 - Differenzen anzeigen
        2 - Schulden anzeigen
        3 - Schuld bezahlen
        0 - Zurück
        
        Auswahl:
        """);
  }

  public void budgetMenue() {
    System.out.println("""
        
        ===== Budgets =====
        
        1 - Budget setzen
        2 - Budgetanalyse anzeigen
        0 - Zurück
        
        Auswahl:
        """);
  }

  public void personenMenue(List<String> personen) {
    System.out.println();
    System.out.println("===== Personen =====");
    System.out.println();

    personen.forEach(person ->
        System.out.println("- " + person));

    System.out.println("""
      
      1 - Ausgaben einer Person anzeigen
      2 - Budget einer Person anzeigen
      3 - Differenz einer Person anzeigen
      4 - Schulden einer Person anzeigen
      5 - Personen Details anzeigen
      0 - Zurück
      
      Auswahl:
      """);
  }

  // Eingabeaufforderungen
  public void nameAbfragen() {
    System.out.print("Name: ");
  }

  public void dateinameAbfragen() {
    System.out.print("Dateiname: ");
  }

  public void betragAbfragen() {
    System.out.print("Betrag: ");
  }

  public void budgetAbfragen() {
    System.out.print("Budget: ");
  }

  public void beschreibungAbfragen() {
    System.out.print("Beschreibung: ");
  }

  public void schuldnerAbfragen() {
    System.out.print("Wer bezahlt?: ");
  }

  public void glaeubigerAbfragen() {
    System.out.print("An wen?: ");
  }

  // Statusmeldungen
  public void ungueltigeEingabe() {
    System.out.println("Ungültige Eingabe.");
  }

  public void erfolgreichGespeichert() {
    System.out.println("Erfolgreich gespeichert.");
  }

  public void programmBeendet() {
    System.out.println("Programm beendet.");
  }

  public void ungueltigeDatei() {
    System.out.println("Ungueltige Datei.");
  }
}
