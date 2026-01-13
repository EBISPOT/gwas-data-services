package uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.CuratorTracking;

import java.util.List;

public interface CuratorTrackingRepository extends JpaRepository<CuratorTracking, Long> {

   List<CuratorTracking> findByStudyId(Long studyId);

}
