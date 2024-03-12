package uk.ac.ebi.spot.gwas.data.copy.table.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.SSTemplateEntryPlaceholder;

import java.util.Optional;

public interface SSTemplateEntryPlaceholderRepository extends MongoRepository<SSTemplateEntryPlaceholder, String> {

    Optional<SSTemplateEntryPlaceholder> findByPmid(String pmid);
}
