package uk.ac.ebi.spot.gwas.data.copy.table.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.ac.ebi.spot.gwas.data.copy.table.model.WeeklyProgressView;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by emma on 08/06/2016.
 *
 * @author emma
 *         <p>
 *         Repository accessing weekly curator progress
 */

public interface WeeklyProgressViewRepository extends JpaRepository<WeeklyProgressView, Long> {
    @Query("SELECT DISTINCT weekStartDay FROM WeeklyProgressView ORDER BY weekStartDay ASC") List<Date> getAllWeekStartDates();

    // Works with String[]
    @Query(value = "SELECT DISTINCT P.PUBMED_ID, listagg(S.ID, ',', ON OVERFLOW TRUNCATE) WITHIN GROUP (ORDER BY S.ID) STUDY_IDS " +
            "FROM STUDY S, PUBLICATION P WHERE P.ID=S.PUBLICATION_ID GROUP BY P.PUBMED_ID" ,
            nativeQuery = true)
    List<Map.Entry> getAllPublicationToStudyMappings();

    // Get Publications and Studies for only Targeted Arrays defined as those from Open Targets
    @Query(value = "SELECT DISTINCT P.PUBMED_ID, listagg(S.ID, ',', ON OVERFLOW TRUNCATE) WITHIN GROUP (ORDER BY S.ID) STUDY_IDS " +
            "FROM STUDY S, PUBLICATION P " +
            "WHERE P.ID=S.PUBLICATION_ID AND S.OPEN_TARGETS=1 " +
            "GROUP BY P.PUBMED_ID", nativeQuery = true)
    List<Map.Entry> getAllPublicationToTargetedArrayStudyMappings();


}
