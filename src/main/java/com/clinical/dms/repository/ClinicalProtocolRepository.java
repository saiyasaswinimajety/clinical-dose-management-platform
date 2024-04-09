package com.clinical.dms.repository;

import com.clinical.dms.model.ClinicalProtocol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicalProtocolRepository extends JpaRepository<ClinicalProtocol, String> {
    Optional<ClinicalProtocol> findByProtocolCode(String protocolCode);
}
