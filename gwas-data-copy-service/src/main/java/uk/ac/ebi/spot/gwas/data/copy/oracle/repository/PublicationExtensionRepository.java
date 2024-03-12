package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.model.PublicationExtension;


public interface PublicationExtensionRepository extends JpaRepository<PublicationExtension, Long> {

}
