package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Region;

/**
 * Created by emma on 01/12/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Region entity object
 */


public interface RegionRepository extends JpaRepository<Region, Long> {

    Region findByName(String regionName);

}
