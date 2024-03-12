package uk.ac.ebi.spot.gwas.data.copy.table.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.table.model.DeletedAncestry;

import java.util.List;

/**
 * Created by emma on 05/08/16.
 *
 * @author emma
 *         <p>
 *         Repository accessing Deleted Ancestry entity object
 */

public interface DeletedAncestryRepository extends JpaRepository<DeletedAncestry, Long> {

    List<DeletedAncestry> findByStudyId(Long id);
}

