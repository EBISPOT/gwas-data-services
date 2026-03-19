package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import uk.ac.ebi.spot.gwas.model.Association;

import javax.persistence.LockModeType;

public interface AssociationRepository extends JpaRepository<Association, Long> {

}
