package uk.ac.ebi.spot.gwas.data.copy.table.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.ac.ebi.spot.gwas.data.copy.table.model.StudyReport;

/**
 * Created by emma on 13/03/15.
 *
 * @author emma
 *         <p>
 *         Repository accessing Study Report entity object
 */

public interface StudyReportRepository extends JpaRepository<StudyReport, Long> {
    StudyReport findByStudyId(Long studyId);
}
