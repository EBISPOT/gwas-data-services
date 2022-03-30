package uk.ac.ebi.spot.gwas.common.model;

import javax.persistence.Entity;


@Entity
public class YearlyTotalsSummaryView extends TotalsSummaryView {

    // JPA no-args constructor
    public YearlyTotalsSummaryView() {
    }

    public YearlyTotalsSummaryView(Integer year, String curator, Integer curatorTotal, String curationStatus) {
        super(year, curator, curatorTotal, curationStatus);
    }
}
