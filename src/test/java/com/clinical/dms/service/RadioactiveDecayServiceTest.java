package com.clinical.dms.service;

import com.clinical.dms.dto.DecayCalculationResult;
import com.clinical.dms.model.Isotope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class RadioactiveDecayServiceTest {

    private RadioactiveDecayService decayService;

    @BeforeEach
    void setUp() {
        decayService = new RadioactiveDecayService();
    }

    @Test
    @DisplayName("Should accurately compute decay constant lambda = ln(2) / t_half")
    void testCalculateDecayConstant() {
        // Fluorine-18: half-life = 109.77 minutes
        double halfLife = 109.77;
        double lambda = decayService.calculateDecayConstant(halfLife);
        double expected = Math.log(2.0) / 109.77;
        assertEquals(expected, lambda, 1e-9);
    }

    @Test
    @DisplayName("Should throw when half-life is non-positive")
    void testInvalidHalfLife() {
        assertThrows(IllegalArgumentException.class, () -> decayService.calculateDecayConstant(0));
        assertThrows(IllegalArgumentException.class, () -> decayService.calculateDecayConstant(-10.5));
    }

    @Test
    @DisplayName("Activity should be exactly 50% after one half-life")
    void testRemainingActivityAfterOneHalfLife() {
        double halfLife = 100.0;
        double initialActivity = 20.0; // 20 mCi
        double remaining = decayService.calculateRemainingActivity(initialActivity, halfLife, 100.0);
        assertEquals(10.0, remaining, 1e-6);
    }

    @Test
    @DisplayName("Activity should be exactly 25% after two half-lives")
    void testRemainingActivityAfterTwoHalfLives() {
        double halfLife = 60.0;
        double initialActivity = 100.0;
        double remaining = decayService.calculateRemainingActivity(initialActivity, halfLife, 120.0);
        assertEquals(25.0, remaining, 1e-6);
    }

    @Test
    @DisplayName("Should compute required synthesis activity to guarantee target dose at future administration time")
    void testCalculateRequiredSynthesisActivity() {
        double halfLife = 109.77; // F-18
        double targetDose = 10.0;  // 10 mCi needed at administration
        double elapsedMinutes = 109.77; // Exactly 1 half-life elapsed

        double requiredSynthesis = decayService.calculateRequiredSynthesisActivity(targetDose, halfLife, elapsedMinutes);
        // At T0, must synthesize 20 mCi so that after 1 half-life, 10 mCi remains
        assertEquals(20.0, requiredSynthesis, 1e-6);
    }

    @Test
    @DisplayName("Should evaluate complete decay schedule between synthesis and injection")
    void testEvaluateDecay() {
        Isotope isotope = new Isotope(
                "ISO-F18", "F-18", "Fluorine-18", 109.77, "mCi"
        );

        Instant synthesisTime = Instant.now();
        Instant targetTime = synthesisTime.plus(219, ChronoUnit.MINUTES); // approx 2 half-lives

        DecayCalculationResult result = decayService.evaluateDecay(isotope, synthesisTime, targetTime, 10.0);

        assertNotNull(result);
        assertEquals("F-18", result.getIsotopeSymbol());
        assertEquals(109.77, result.getHalfLifeMinutes());
        assertTrue(result.getElapsedMinutes() >= 218.0 && result.getElapsedMinutes() <= 220.0);
        // Synthesis activity should be ~ 40 mCi (since ~2 half-lives elapsed)
        assertTrue(result.getRequiredSynthesisActivityMci() > 39.0 && result.getRequiredSynthesisActivityMci() < 41.0);
        assertTrue(result.getDecayFactor() > 0.24 && result.getDecayFactor() < 0.26);
    }
}
