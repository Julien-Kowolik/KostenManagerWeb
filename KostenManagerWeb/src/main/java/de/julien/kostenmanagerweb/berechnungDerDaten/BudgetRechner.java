package de.julien.kostenmanagerweb.berechnungDerDaten;

import java.util.Map;

public class BudgetRechner {

  // Budget-Summen berechnen
  public double getBudgetSum(Map<String, Double> budgets) {
    return budgets.values()
        .stream()
        .mapToDouble(Double::doubleValue)
        .sum();
  }

  public double getBudgetRestSum(Map<String, Double> budgets, Map<String, Double> ausgaben) {
    double budget = getBudgetSum(budgets);
    double augsaben = getBudgetSum(ausgaben);
    return budget - augsaben;
  }
}
