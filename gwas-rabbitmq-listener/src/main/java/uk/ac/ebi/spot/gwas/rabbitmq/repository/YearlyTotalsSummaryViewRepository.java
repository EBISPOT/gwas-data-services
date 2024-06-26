package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.ac.ebi.spot.gwas.model.YearlyTotalsSummaryView;

import java.util.List;

/**
 * Created by emma on 22/09/2015.
 *
 * @author emma
 *         <p>
 *         Repository accessing yearly curator totals view
 */

public interface YearlyTotalsSummaryViewRepository extends JpaRepository<YearlyTotalsSummaryView, Long> {
    @Query("select distinct year from YearlyTotalsSummaryView order by year desc") List<Integer> getAllYears();

    List<YearlyTotalsSummaryView> findByCuratorAndCurationStatusAndYearOrderByYearDesc(String curatorName,
                                                                                       String statusName,
                                                                                       Integer year);

    List<YearlyTotalsSummaryView> findByCuratorAndCurationStatus(String curatorName, String statusName);

    List<YearlyTotalsSummaryView> findByCurationStatusAndYearOrderByYearDesc(String statusName, Integer year);

    List<YearlyTotalsSummaryView> findByCurationStatus(String statusName);

    List<YearlyTotalsSummaryView> findByCuratorAndYearOrderByYearDesc(String curatorName, Integer year);

    List<YearlyTotalsSummaryView> findByCurator(String curatorName);

    List<YearlyTotalsSummaryView> findByYearOrderByYearDesc(Integer year);
}
