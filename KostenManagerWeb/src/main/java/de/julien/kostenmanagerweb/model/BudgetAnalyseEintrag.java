package de.julien.kostenmanagerweb.model;

public record BudgetAnalyseEintrag(
    String name,
    double budget,
    double ausgegeben,
    double restBudget,
    boolean ueberschritten) {
}
