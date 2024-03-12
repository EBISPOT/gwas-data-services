package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.model.Region;

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
