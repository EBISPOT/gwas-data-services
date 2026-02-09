package uk.ac.ebi.spot.gwas.submission.oracle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Association;

public interface AssociationOracleRepository extends JpaRepository<Association, Long> {

    Long countByStudyId(Long studyId);

    Page<Association> findByStudyId(Long studyId, Pageable pageable);
}
