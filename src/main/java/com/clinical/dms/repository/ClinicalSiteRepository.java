package com.clinical.dms.repository;

import com.clinical.dms.model.ClinicalSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicalSiteRepository extends JpaRepository<ClinicalSite, String> {
    Optional<ClinicalSite> findBySiteCode(String siteCode);
}
