package com.clinical.dms.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "isotopes")
public class Isotope {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, unique = true, length = 16)
    private String symbol;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(name = "half_life_minutes", nullable = false)
    private double halfLifeMinutes;

    @Column(name = "base_unit", nullable = false, length = 16)
    private String baseUnit = "mCi";

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public Isotope() {}

    public Isotope(String id, String symbol, String name, double halfLifeMinutes, String baseUnit) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.halfLifeMinutes = halfLifeMinutes;
        this.baseUnit = baseUnit;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getHalfLifeMinutes() { return halfLifeMinutes; }
    public void setHalfLifeMinutes(double halfLifeMinutes) { this.halfLifeMinutes = halfLifeMinutes; }

    public String getBaseUnit() { return baseUnit; }
    public void setBaseUnit(String baseUnit) { this.baseUnit = baseUnit; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
