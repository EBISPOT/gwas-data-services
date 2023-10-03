package uk.ac.ebi.spot.gwas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.model.Location;
import uk.ac.ebi.spot.gwas.model.MappingProjection;

import java.util.List;


@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByChromosomeNameAndChromosomePositionAndRegionName(String chromosomeName,
                                                                    Integer chromosomePosition,
                                                                    String regionName);
    @Query("select loc.chromosomeName as chromosomeName, loc.chromosomePosition as chromosomePosition, r.name as regionName from Location as loc " +
            "JOIN loc.snps snp " +
            "JOIN loc.region as r " +
            "where  length(loc.chromosomeName) < 3 and snp.id = :snpId")
    List<MappingProjection> getLocationDetails(Long snpId);

}
