package uk.ac.ebi.spot.gwas.data.copy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.PublicationIngestEntry;

import java.util.List;

public interface PublicationIngestEntryRepository extends MongoRepository<PublicationIngestEntry, String> {
    List<PublicationIngestEntry> findByEnvironment(String env);
}
