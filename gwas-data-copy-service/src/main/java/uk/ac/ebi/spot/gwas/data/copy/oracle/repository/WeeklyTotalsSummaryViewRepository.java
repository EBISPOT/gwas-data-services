package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import uk.ac.ebi.spot.gwas.data.copy.model.WeeklyTotalsSummaryView;

import java.util.List;

/**
 * Created by dwelter on 15/04/16.
 */

public interface WeeklyTotalsSummaryViewRepository extends JpaRepository<WeeklyTotalsSummaryView, Long> {

   @Query("select w from WeeklyTotalsSummaryView w where rownum < 9") List<WeeklyTotalsSummaryView> getLastEightWeeks();



}
