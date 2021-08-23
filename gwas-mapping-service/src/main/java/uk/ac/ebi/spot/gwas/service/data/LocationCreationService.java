package uk.ac.ebi.spot.gwas.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.Location;
import uk.ac.ebi.spot.gwas.model.Region;
import uk.ac.ebi.spot.gwas.repository.LocationRepository;
import uk.ac.ebi.spot.gwas.repository.RegionRepository;

@Service
public class LocationCreationService {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private RegionRepository regionRepository;

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
