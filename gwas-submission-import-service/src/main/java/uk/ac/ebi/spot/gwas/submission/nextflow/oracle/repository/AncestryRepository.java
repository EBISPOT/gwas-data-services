package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Ancestry;

import java.util.List;

public interface AncestryRepository extends JpaRepository<Ancestry, Long> {

  List<Ancestry> findByStudyId(Long studyId);


}
