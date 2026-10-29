package com.clinical.coms.repository;

import com.clinical.coms.model.Isotope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IsotopeRepository extends JpaRepository<Isotope, String> {
    Optional<Isotope> findBySymbol(String symbol);
}
