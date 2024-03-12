package uk.ac.ebi.spot.gwas.data.copy.table.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.SummaryStatsEntry;

import java.util.List;


public interface SummaryStatsEntryRepository extends MongoRepository<SummaryStatsEntry, String> {

    List<SummaryStatsEntry> findByFileUploadId(String fileId);

    List<SummaryStatsEntry> findByCallbackId(String callbackId);
}
