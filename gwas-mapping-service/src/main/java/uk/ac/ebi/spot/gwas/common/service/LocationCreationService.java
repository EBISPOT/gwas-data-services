package uk.ac.ebi.spot.gwas.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.common.model.Location;
import uk.ac.ebi.spot.gwas.common.model.Region;
import uk.ac.ebi.spot.gwas.common.repository.LocationRepository;
import uk.ac.ebi.spot.gwas.common.repository.RegionRepository;

@Service
public class LocationCreationService {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Transactional(propagation = Propagation.SUPPORTS)
    public Location createLocation(String chromosomeName, Integer chromosomePosition, String regionName) {
        Region region = null;
        region = regionRepository.findByName(regionName);
        if (region == null) {
            Region newRegion = new Region();
            newRegion.setName(regionName);
            region = regionRepository.save(newRegion);
        }

        Location newLocation = new Location();
        newLocation.setChromosomeName(chromosomeName);
        newLocation.setChromosomePosition(chromosomePosition);
        newLocation.setRegion(region);

        // Save location
        locationRepository.save(newLocation);
        return newLocation;
    }
}
