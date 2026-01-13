package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import clojure.lang.IFn;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Curator;

import java.util.Optional;

public interface CuratorRepository extends JpaRepository<Curator, Long> {

   Optional<Curator> findByEmail(String email);

   Optional<Curator> findByLastName(String lastName);
}
