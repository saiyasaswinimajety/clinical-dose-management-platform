package com.clinical.dms.service;

import com.clinical.dms.dto.DecayCalculationResult;
import com.clinical.dms.model.Isotope;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * Precision Nuclear Physics & Radiopharmaceutical Decay Engine.
 * Implements exponential radioactive decay law: A(t) = A0 * e^(-lambda * t)
 * where lambda = ln(2) / t_half.
 */
@Service
public class RadioactiveDecayService {

    private static final double LN_2 = Math.log(2.0);

    /**
     * Calculates decay constant lambda in 1/minutes.
     */
    public double calculateDecayConstant(double halfLifeMinutes) {
        if (halfLifeMinutes <= 0) {
            throw new IllegalArgumentException("Half-life must be strictly positive");
        }
        return LN_2 / halfLifeMinutes;
    }

    /**
     * Computes remaining activity after elapsed time: A(t) = A0 * exp(-lambda * t)
     */
    public double calculateRemainingActivity(double initialActivityMci, double halfLifeMinutes, double elapsedMinutes) {
        if (initialActivityMci < 0 || elapsedMinutes < 0) {
            throw new IllegalArgumentException("Activity and elapsed time cannot be negative");
        }
        double lambda = calculateDecayConstant(halfLifeMinutes);
        return initialActivityMci * Math.exp(-lambda * elapsedMinutes);
    }

    /**
     * Computes required initial synthesis activity at time T0 to guarantee target activity at time T1:
     * A0 = A_target / exp(-lambda * elapsedMinutes) = A_target * exp(lambda * elapsedMinutes)
     */
    public double calculateRequiredSynthesisActivity(double targetActivityMci, double halfLifeMinutes, double elapsedMinutes) {
        if (targetActivityMci <= 0 || elapsedMinutes < 0) {
            throw new IllegalArgumentException("Target activity must be positive, elapsed time cannot be negative");
        }
        double lambda = calculateDecayConstant(halfLifeMinutes);
        return targetActivityMci * Math.exp(lambda * elapsedMinutes);
    }

    /**
     * Full evaluation result between synthesis time and administration time.
     */
    public DecayCalculationResult evaluateDecay(Isotope isotope, Instant synthesisTime, Instant targetTime, double orderedActivityMci) {
        double elapsedMinutes = Duration.between(synthesisTime, targetTime).toSeconds() / 60.0;
        if (elapsedMinutes < 0) {
            elapsedMinutes = 0;
        }

        double lambda = calculateDecayConstant(isotope.getHalfLifeMinutes());
        double decayFactor = Math.exp(-lambda * elapsedMinutes);
        double requiredSynthesis = calculateRequiredSynthesisActivity(orderedActivityMci, isotope.getHalfLifeMinutes(), elapsedMinutes);
        double remainingIfProducedAtTarget = calculateRemainingActivity(orderedActivityMci, isotope.getHalfLifeMinutes(), elapsedMinutes);

        return new DecayCalculationResult(
                isotope.getSymbol(),
                isotope.getHalfLifeMinutes(),
                synthesisTime,
                targetTime,
                Math.round(elapsedMinutes * 100.0) / 100.0,
                Math.round(requiredSynthesis * 100.0) / 100.0,
                Math.round(remainingIfProducedAtTarget * 100.0) / 100.0,
                Math.round(requiredSynthesis * 100.0) / 100.0,
                Math.round(decayFactor * 10000.0) / 10000.0
        );
    }
}
