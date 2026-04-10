package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Publication;

import java.util.Optional;

public interface PublicationRepository extends JpaRepository<Publication, Long>  {

   Optional<Publication> findByPubmedId(String pubmedId);
}
