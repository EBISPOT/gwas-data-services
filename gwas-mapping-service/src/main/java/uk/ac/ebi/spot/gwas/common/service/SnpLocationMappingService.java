package uk.ac.ebi.spot.gwas.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.common.model.GenomicContext;
import uk.ac.ebi.spot.gwas.common.model.Location;
import uk.ac.ebi.spot.gwas.common.model.Region;
import uk.ac.ebi.spot.gwas.common.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.common.repository.GenomicContextRepository;
import uk.ac.ebi.spot.gwas.common.repository.LocationRepository;
import uk.ac.ebi.spot.gwas.common.repository.SingleNucleotidePolymorphismRepository;

import java.util.*;

@Service
public class SnpLocationMappingService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    @Autowired
    private GenomicContextRepository genomicContextRepository;
    @Autowired
    private SingleNucleotidePolymorphismQueryService singleNucleotidePolymorphismQueryService;
    @Autowired
    private LocationCreationService locationCreationService;

    @Transactional(propagation = Propagation.SUPPORTS)
    public void storeSnpLocation(Map<String, Set<Location>> snpToLocations) {



        // Go through each rs_id and its associated locations returned from the mapping pipeline
        for (String snpRsId : snpToLocations.keySet()) {

            log.info("SnpId to be saved is ->"+snpRsId);
            Set<Location> snpLocationsFromMapping = snpToLocations.get(snpRsId);

            // Check if the SNP exists
            SingleNucleotidePolymorphism snpInDatabase =
                    singleNucleotidePolymorphismRepository.findByRsId(snpRsId);
            if(snpInDatabase == null){
                snpInDatabase =
                        singleNucleotidePolymorphismQueryService.findByRsIdIgnoreCase(snpRsId);
            }

            if (snpInDatabase != null) {

                // Store all new location objects
                Collection<Location> newSnpLocations = new ArrayList<>();

                for (Location snpLocationFromMapping : snpLocationsFromMapping) {

                    String chromosomeNameFromMapping = snpLocationFromMapping.getChromosomeName();
                    log.info("chromosomeName from Location mapping ->"+chromosomeNameFromMapping);
                    if (chromosomeNameFromMapping != null) {
                        chromosomeNameFromMapping = chromosomeNameFromMapping.trim();
                    }

                    Integer chromosomePositionFromMapping = snpLocationFromMapping.getChromosomePosition();
                    Region regionFromMapping = snpLocationFromMapping.getRegion();
                    String regionNameFromMapping = null;
                    if (regionFromMapping != null) {
                        if (regionFromMapping.getName() != null) {
                            regionNameFromMapping = regionFromMapping.getName().trim();
                        }
                    }

                    Location existingLocation =
                            locationRepository.findByChromosomeNameAndChromosomePositionAndRegionName(
                                    chromosomeNameFromMapping,
                                    chromosomePositionFromMapping,
                                    regionNameFromMapping);


                    if (existingLocation != null) {
                        newSnpLocations.add(existingLocation);
                    }else {
                        Location newLocation = locationCreationService.createLocation(chromosomeNameFromMapping,
                                                                                      chromosomePositionFromMapping,
                                                                                      regionNameFromMapping);
                        newSnpLocations.add(newLocation);
                    }
                }

                // If we have new locations then link to snp and save
                if (newSnpLocations.size() > 0) {
                    snpInDatabase.setLocations(newSnpLocations);
                    snpInDatabase.setLastUpdateDate(new Date());
                    singleNucleotidePolymorphismRepository.save(snpInDatabase);
                }
                else {
                    log.warn("No new locations to add to " + snpRsId);}

            }

            // SNP doesn't exist, this should be extremely rare as SNP value is a copy
            // of the variant entered by the curator which
            // by the time mapping is started should already have been saved
            else {
                // TODO WHAT WILL HAPPEN FOR MERGED SNPS
                log.error("Adding location for SNP not found in database, RS_ID:" + snpRsId);
                throw new RuntimeException("Adding location for SNP not found in database, RS_ID: " + snpRsId);
            }

        }
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public void removeExistingSnpLocations(SingleNucleotidePolymorphism snp) {

        // Get a list of locations currently linked to SNP
        Collection<Location> oldSnpLocations = snp.getLocations();

        if (oldSnpLocations != null && !oldSnpLocations.isEmpty()) {
            Set<Long> oldSnpLocationIds = new HashSet<>();
            for (Location oldSnpLocation : oldSnpLocations) {
                oldSnpLocationIds.add(oldSnpLocation.getId());
            }

            // Remove old locations
            snp.setLocations(new ArrayList<>());
            singleNucleotidePolymorphismRepository.save(snp);

            // Clean-up old locations that were linked to SNP
            if (oldSnpLocationIds.size() > 0) {
                for (Long oldSnpLocationId : oldSnpLocationIds) {
                    cleanUpLocations(oldSnpLocationId);
                }
            }
        }
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public void cleanUpLocations(Long id) {
        List<SingleNucleotidePolymorphism> snps =
                singleNucleotidePolymorphismRepository.findIdsByLocationId(id);
        List<GenomicContext> genomicContexts = genomicContextRepository.findIdsByLocationId(id);

        if (snps.size() == 0 && genomicContexts.size() == 0) {
            locationRepository.deleteById(id);
        }
    }

}
