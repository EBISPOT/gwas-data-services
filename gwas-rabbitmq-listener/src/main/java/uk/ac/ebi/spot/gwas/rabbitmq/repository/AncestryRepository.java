package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Ancestry;

import java.util.Collection;

/**
 * Created by emma on 28/11/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Ancestry entity object
 */

public interface AncestryRepository extends JpaRepository<Ancestry, Long> {
    Collection<Ancestry> findByStudyIdAndType(Long studyId, String Type);

    Collection<Ancestry> findByStudyId(Long studyId);

}
