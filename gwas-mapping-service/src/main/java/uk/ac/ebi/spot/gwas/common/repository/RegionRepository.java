package uk.ac.ebi.spot.gwas.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.common.model.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    Region findByName(String regionName);

}
