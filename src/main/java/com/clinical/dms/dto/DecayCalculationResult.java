package com.clinical.dms.dto;

import java.time.Instant;

public class DecayCalculationResult {

    private String isotopeSymbol;
    private double halfLifeMinutes;
    private Instant synthesisTime;
    private Instant targetAdministrationTime;
    private double elapsedMinutes;
    private double initialActivityMci;
    private double remainingActivityMci;
    private double requiredProductionActivityMci;
    private double decayFactor;

    public DecayCalculationResult() {}

    public DecayCalculationResult(String isotopeSymbol, double halfLifeMinutes, Instant synthesisTime,
                                  Instant targetAdministrationTime, double elapsedMinutes,
                                  double initialActivityMci, double remainingActivityMci,
                                  double requiredProductionActivityMci, double decayFactor) {
        this.isotopeSymbol = isotopeSymbol;
        this.halfLifeMinutes = halfLifeMinutes;
        this.synthesisTime = synthesisTime;
        this.targetAdministrationTime = targetAdministrationTime;
        this.elapsedMinutes = elapsedMinutes;
        this.initialActivityMci = initialActivityMci;
        this.remainingActivityMci = remainingActivityMci;
        this.requiredProductionActivityMci = requiredProductionActivityMci;
        this.decayFactor = decayFactor;
    }

    public String getIsotopeSymbol() { return isotopeSymbol; }
    public double getHalfLifeMinutes() { return halfLifeMinutes; }
    public Instant getSynthesisTime() { return synthesisTime; }
    public Instant getTargetAdministrationTime() { return targetAdministrationTime; }
    public double getElapsedMinutes() { return elapsedMinutes; }
    public double getInitialActivityMci() { return initialActivityMci; }
    public double getRemainingActivityMci() { return remainingActivityMci; }
    public double getRequiredProductionActivityMci() { return requiredProductionActivityMci; }
    public double getRequiredSynthesisActivityMci() { return requiredProductionActivityMci; }
    public double getDecayFactor() { return decayFactor; }
}
