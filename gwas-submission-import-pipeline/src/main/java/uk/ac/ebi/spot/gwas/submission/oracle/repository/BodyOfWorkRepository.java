package uk.ac.ebi.spot.gwas.submission.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.BodyOfWork;

public interface BodyOfWorkRepository extends JpaRepository<BodyOfWork, Long> {
}
