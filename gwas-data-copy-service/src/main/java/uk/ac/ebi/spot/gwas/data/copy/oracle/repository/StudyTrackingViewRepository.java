package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.model.StudyTrackingView;

/**
 * Created by Cinzia on 8/11/16.
 *
 * @author Cinzia
 *         <p>
 *        Repository for Study Tracking view
 */

public interface StudyTrackingViewRepository extends JpaRepository<StudyTrackingView, Long>{

}
