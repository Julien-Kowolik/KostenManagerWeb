package de.julien.kostenmanagerweb.dateiLeser;

import de.julien.kostenmanagerweb.model.Ausgabe;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CsvAusgabenLeser {

  // CSV-Datei einlesen
  public static List<Ausgabe> leseDatei(String dateiName, List<String> personen) {
    List<String> zeien = getZeilen(dateiName);

    return zeien.stream()
        .map(z -> z.split(","))
        .filter(z -> z.length == 3)
        .filter(z -> istDabei(z[0].replace("\uFEFF", "").trim(), personen))
        .filter(z -> !z[1].isBlank() && !z[0].isBlank())
        .map(CsvAusgabenLeser::parseAusgabe)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  // Zeilen parsen und prüfen
  private static Ausgabe parseAusgabe(String[] split) {
    try {
      return new Ausgabe(
          split[0].replace("\uFEFF", "").trim(),
          Math.abs(Double.parseDouble(split[1].trim())),
          split[2].trim()
      );
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static boolean istDabei(String name, List<String> personen) {
    return personen.stream()
        .anyMatch(person -> person.equalsIgnoreCase(name));
  }

  // Datei laden
  private static List<String> getZeilen(String dateiName) {
    List<String> zeien;
    try {
      zeien = Files.readAllLines(Paths.get(dateiName), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalArgumentException(
          "Datei konnte nicht gelesen werden."
      );
    }
    return zeien;
  }
}
