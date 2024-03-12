package uk.ac.ebi.spot.gwas.data.copy.table.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.gwas.deposition.domain.DailyStats;

import java.util.List;

public interface DailyStatsRepository extends MongoRepository<DailyStats, String> {
    List<DailyStats> findAllByOrderByDateDesc(Pageable pageable);
}
