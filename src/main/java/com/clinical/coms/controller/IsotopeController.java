package com.clinical.coms.controller;

import com.clinical.coms.dto.DecayCalculationResult;
import com.clinical.coms.model.Isotope;
import com.clinical.coms.repository.IsotopeRepository;
import com.clinical.coms.service.RadioactiveDecayService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/isotopes")
public class IsotopeController {

    private final IsotopeRepository isotopeRepository;
    private final RadioactiveDecayService decayService;

    public IsotopeController(IsotopeRepository isotopeRepository, RadioactiveDecayService decayService) {
        this.isotopeRepository = isotopeRepository;
        this.decayService = decayService;
    }

    @GetMapping
    public ResponseEntity<List<Isotope>> listIsotopes() {
        return ResponseEntity.ok(isotopeRepository.findAll());
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<Isotope> getIsotopeBySymbol(@PathVariable String symbol) {
        return isotopeRepository.findBySymbol(symbol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{symbol}/decay-estimate")
    public ResponseEntity<DecayCalculationResult> estimateDecay(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant synthesisTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant targetTime,
            @RequestParam double orderedActivityMci) {
        
        Isotope isotope = isotopeRepository.findBySymbol(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Unknown isotope: " + symbol));

        DecayCalculationResult result = decayService.evaluateDecay(isotope, synthesisTime, targetTime, orderedActivityMci);
        return ResponseEntity.ok(result);
    }
}
