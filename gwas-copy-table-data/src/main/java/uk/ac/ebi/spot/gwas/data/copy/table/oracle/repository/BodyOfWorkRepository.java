package uk.ac.ebi.spot.gwas.data.copy.table.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.table.model.BodyOfWork;


public interface BodyOfWorkRepository extends JpaRepository<BodyOfWork, Long> {
    BodyOfWork findByPublicationId(String publicationId);
    BodyOfWork findByPubMedId(String pubMedId);
}
