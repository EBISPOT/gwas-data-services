package uk.ac.ebi.spot.gwas.data.copy.table.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.CompletedSubmission;

public interface CompletedSubmissionRepository extends MongoRepository<CompletedSubmission, String> {

}
