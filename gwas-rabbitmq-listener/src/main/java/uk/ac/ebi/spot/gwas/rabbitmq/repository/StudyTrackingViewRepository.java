package uk.ac.ebi.spot.gwas.rabbitmq.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.StudyTrackingView;

/**
 * Created by Cinzia on 8/11/16.
 *
 * @author Cinzia
 *         <p>
 *        Repository for Study Tracking view
 */

public interface StudyTrackingViewRepository extends JpaRepository<StudyTrackingView, Long>{

}
