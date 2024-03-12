package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.ac.ebi.spot.gwas.data.copy.model.UnpublishedStudy;


public interface UnpublishedStudyRepository extends JpaRepository<UnpublishedStudy, Long> {

    UnpublishedStudy findByAccession(String accessionID);

    @Query("SELECT U FROM UnpublishedStudy U WHERE U.summaryStatsFile <> 'NR' ")
    Page<UnpublishedStudy> findBySummaryStatsFileIsNotRequired(Pageable pageable);
}
