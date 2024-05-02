package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Curator;

/**
 * Created by emma on 27/11/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Curator entity object
 */

public interface CuratorRepository extends JpaRepository<Curator, Long> {

    Curator findByLastName(String lastName);

    Curator findByLastNameIgnoreCase(String lastName);

    Curator findByEmail(String email);
}
