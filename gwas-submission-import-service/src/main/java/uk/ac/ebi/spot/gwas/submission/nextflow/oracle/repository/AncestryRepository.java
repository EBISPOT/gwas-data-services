package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import uk.ac.ebi.spot.gwas.model.Ancestry;

import javax.persistence.LockModeType;
import java.util.List;

public interface AncestryRepository extends JpaRepository<Ancestry, Long> {


}
