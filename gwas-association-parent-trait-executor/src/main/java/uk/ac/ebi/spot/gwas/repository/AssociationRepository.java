package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Association;

public interface AssociationRepository extends JpaRepository<Association, Long> {


}
