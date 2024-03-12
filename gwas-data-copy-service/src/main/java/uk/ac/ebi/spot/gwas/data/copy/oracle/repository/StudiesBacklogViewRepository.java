package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.model.StudiesBacklogView;

/**
 * Created by cinzia on 31/10/2016.
 */



public interface StudiesBacklogViewRepository extends JpaRepository<StudiesBacklogView, Long> {

}
