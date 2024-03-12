package uk.ac.ebi.spot.gwas.data.copy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.CurationStatus;

import java.util.Optional;

public interface CurationStatusRepository extends MongoRepository<CurationStatus, String> {

    public Optional<CurationStatus> findById(String id);
    public Optional<CurationStatus> findByStatus(String status);

}
