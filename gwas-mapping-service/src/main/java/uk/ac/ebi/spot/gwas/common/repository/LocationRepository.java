package uk.ac.ebi.spot.gwas.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.common.model.Location;


@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByChromosomeNameAndChromosomePositionAndRegionName(String chromosomeName,
                                                                    Integer chromosomePosition,
                                                                    String regionName);
}
