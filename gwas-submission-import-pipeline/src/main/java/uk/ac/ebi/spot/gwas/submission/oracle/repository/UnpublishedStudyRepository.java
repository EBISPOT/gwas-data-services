package uk.ac.ebi.spot.gwas.submission.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.UnpublishedStudy;

import java.util.Optional;

public interface UnpublishedStudyRepository extends JpaRepository<UnpublishedStudy, Long> {

   Optional<UnpublishedStudy> findByAccession(String accession);
}
