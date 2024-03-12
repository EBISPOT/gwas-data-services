package uk.ac.ebi.spot.gwas.data.copy.table.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.table.model.AncestralGroup;

/**
 * Created by Dani on 13/04/2017.
 */


public interface AncestralGroupRepository extends JpaRepository<AncestralGroup, Long> {

    AncestralGroup findByAncestralGroup(String ancestralGroup);
}
