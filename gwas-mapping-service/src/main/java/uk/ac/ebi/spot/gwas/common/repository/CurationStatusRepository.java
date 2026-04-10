package uk.ac.ebi.spot.gwas.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.common.model.CurationStatus;

public interface CurationStatusRepository extends JpaRepository<CurationStatus, Long> {

    CurationStatus findByStatus(String status);
}
