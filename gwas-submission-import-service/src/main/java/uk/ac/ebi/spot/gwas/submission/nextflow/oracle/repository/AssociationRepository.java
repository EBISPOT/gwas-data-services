package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Association;

public interface AssociationRepository extends JpaRepository<Association, Long> {

   Page<Association> findByStudyId(Long studyId, Pageable pageable);


   Long countByStudyId(Long studyId);

}
