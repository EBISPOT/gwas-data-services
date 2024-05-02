package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.DeletedAncestry;

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

