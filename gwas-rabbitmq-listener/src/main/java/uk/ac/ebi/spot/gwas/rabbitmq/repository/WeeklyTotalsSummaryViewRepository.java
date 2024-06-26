package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.ac.ebi.spot.gwas.model.WeeklyTotalsSummaryView;

import java.util.List;

/**
 * Created by dwelter on 15/04/16.
 */

public interface WeeklyTotalsSummaryViewRepository extends JpaRepository<WeeklyTotalsSummaryView, Long> {

   @Query("select w from WeeklyTotalsSummaryView w where rownum < 9") List<WeeklyTotalsSummaryView> getLastEightWeeks();



}
