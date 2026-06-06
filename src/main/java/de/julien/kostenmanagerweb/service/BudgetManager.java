package de.julien.kostenmanagerweb.service;

import de.julien.kostenmanagerweb.berechnungDerDaten.BudgetRechner;
import java.util.HashMap;
import java.util.Map;

class BudgetManager {
  // Grunddaten und Rechner
  private final Map<String, Double> budgets = new HashMap<>();
  private final BudgetRechner rechner = new BudgetRechner();


  // Einzelne Budgets verwalten
  boolean hatBudget(String name) {
    return budgets.containsKey(name);
  }

  void setBudget(String name, Double budget) {
    budgets.put(name, budget);
  }

  double getBudget(String name) {
    if (hatBudget(name)) {
      return budgets.get(name);
    }
    return 0;
  }

  double getRestBudget(String name, double ausgegeben) {
    if (hatBudget(name)) {
      return getBudget(name) - ausgegeben;
    }
    return 0;
  }

  // Budgetüberschreitungen prüfen
  boolean istBudgetUeberschritten(String name, double ausgegeben) {
      return (hatBudget(name) && getRestBudget(name, ausgegeben) < 0);
  }

  double getBudgetUeberschreitung(String name, double ausgegeben) {
    if(!hatBudget(name)) {
      return 0;
    }
    return Math.max(ausgegeben - getBudget(name), 0);
  }

  // Gruppenbudget berechnen
  double getGruppenBudget() {
    return rechner.getBudgetSum(budgets);
  }

  double getGruppenRestBudget(Map<String, Double> ausgaben) {
    return rechner.getBudgetRestSum(budgets, ausgaben);
  }
}
