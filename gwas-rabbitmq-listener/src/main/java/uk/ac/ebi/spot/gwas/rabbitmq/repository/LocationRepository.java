package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Location;

/**
 * Created by Laurent on 18/05/15.
 * <p>
 * * @author lgil
 * <p>
 * Repository accessing Location entity object
 */

public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByChromosomeNameAndChromosomePositionAndRegionName(String chromosomeName,
                                                                    Integer chromosomePosition,
                                                                    String regionName);
}
