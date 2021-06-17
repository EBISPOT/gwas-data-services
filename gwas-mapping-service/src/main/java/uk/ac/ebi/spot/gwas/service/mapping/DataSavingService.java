package uk.ac.ebi.spot.gwas.service.mapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.dto.EnsemblMappingResult;
import uk.ac.ebi.spot.gwas.dto.MappingDto;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.GenomicContext;
import uk.ac.ebi.spot.gwas.model.Location;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.gwas.service.data.AssociationReportService;
import uk.ac.ebi.spot.gwas.service.data.SingleNucleotidePolymorphismQueryService;
import uk.ac.ebi.spot.gwas.service.data.SnpGenomicContextMappingService;
import uk.ac.ebi.spot.gwas.service.data.SnpLocationMappingService;

import java.util.*;

@Slf4j
@Service
public class DataSavingService {

    @Autowired
    private SnpLocationMappingService snpLocationMappingService;
    @Autowired
    private SnpGenomicContextMappingService snpGenomicContextMappingService;
    @Autowired
    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    @Autowired
    private SingleNucleotidePolymorphismQueryService snpQueryService;
    @Autowired
    private AssociationReportService associationReportService;

    public MappingDto saveMappedData(SingleNucleotidePolymorphism snpLinkedToLocus, EnsemblMappingResult ensemblMappingResult) {

        // Map to store returned location data, this is used in snpLocationMappingService to process all locations linked to a single snp in one go
        Map<String, Set<Location>> snpToLocationsMap = new HashMap<>();
        // Collection to store all genomic contexts
        Collection<GenomicContext> allGenomicContexts = new ArrayList<>();
        // Collection to store all errors for one association
        Collection<String> associationPipelineErrors = new ArrayList<>();


        String snpRsId = snpLinkedToLocus.getRsId();
        snpLocationMappingService.removeExistingSnpLocations(snpLinkedToLocus);
        snpGenomicContextMappingService.removeExistingGenomicContexts(snpLinkedToLocus);
        log.info("Removed existing data.");

        // Get new Locations, genomic contexts, errors, functionalClass, merged, currentSnpId
        Collection<Location> locations = ensemblMappingResult.getLocations();
        Collection<GenomicContext> snpGenomicContexts = ensemblMappingResult.getGenomicContexts();
        ArrayList<String> pipelineErrors = ensemblMappingResult.getPipelineErrors();
        String functionalClass = ensemblMappingResult.getFunctionalClass();
        Long merged = Long.valueOf(ensemblMappingResult.getMerged());
        String currentSnpId = ensemblMappingResult.getCurrentSnpId();

        // Update functional class, update date, merged
        snpLinkedToLocus.setFunctionalClass(functionalClass);
        snpLinkedToLocus.setLastUpdateDate(new Date());
        snpLinkedToLocus.setMerged(merged);

        // Update the merge table
        if (merged == 1) {
            SingleNucleotidePolymorphism currentSnp = singleNucleotidePolymorphismRepository.findByRsId(currentSnpId);
            // Create a new entry in the SingleNucleotidePolymorphism SQL table for the current rsID, Add the current SingleNucleotidePolymorphism to the "merged" rsID
            log.info("Looking for merged data: {}", currentSnp);

            if (currentSnp == null) {
                log.info("[{}] Current SNP not null ... saving new data ...", currentSnp);
                currentSnp = new SingleNucleotidePolymorphism();
                currentSnp.setRsId(currentSnpId);
                currentSnp.setFunctionalClass(functionalClass);
                currentSnp = singleNucleotidePolymorphismRepository.save(currentSnp);
                log.info("[{}] Current SNP saved..", currentSnp);
                // currentSnp = singleNucleotidePolymorphismRepository.findByRsId(currentSnpId);
            }
            snpLinkedToLocus.setCurrentSnp(currentSnp);
        }

        log.info("[{}] Saving SNP linked to locus.", snpLinkedToLocus.getRsId());
        singleNucleotidePolymorphismRepository.save(snpLinkedToLocus);
        log.info("[{}] SNP linked to locus saved.", snpLinkedToLocus.getRsId());

        // Store location information for SNP
        if (!locations.isEmpty()) {
            for (Location location : locations) {
                // Next time we see SNP, add location to set. This would only occur if SNP has multiple locations
                if (snpToLocationsMap.containsKey(snpRsId)) {
                    snpToLocationsMap.get(snpRsId).add(location);
                } else {
                    // First time we see a SNP store the location
                    Set<Location> snpLocation = new HashSet<>();
                    snpLocation.add(location);
                    snpToLocationsMap.put(snpRsId, snpLocation);
                }
            }
        } else {
            log.warn("Attempt to map SNP: " + snpRsId + " returned no location details");
            pipelineErrors.add("Attempt to map SNP: " + snpRsId + " returned no location details");
        }
        // Store genomic context data for snp
        if (!snpGenomicContexts.isEmpty()) {
            allGenomicContexts.addAll(snpGenomicContexts);
        } else {
            log.warn("Attempt to map SNP: " + snpRsId + " returned no mapped genes");
            pipelineErrors.add("Attempt to map SNP: " + snpRsId + " returned no mapped genes");
        }

        if (!pipelineErrors.isEmpty()) {
            associationPipelineErrors.addAll(pipelineErrors);
        }

        return MappingDto.builder()
                .allGenomicContexts(allGenomicContexts)
                .snpToLocationsMap(snpToLocationsMap)
                .associationPipelineErrors(associationPipelineErrors)
                .build();
    }


    void createAssociationReports(Association association, MappingDto mappingDto){

        Collection<GenomicContext> allGenomicContexts = mappingDto.getAllGenomicContexts();
        Map<String, Set<Location>> snpToLocationsMap = mappingDto.getSnpToLocationsMap();
        Collection<String> associationPipelineErrors = mappingDto.getAssociationPipelineErrors();

        // Create association report based on whether there is errors or not
        if (!associationPipelineErrors.isEmpty()) {
            log.info("Processing association errors");
            associationReportService.processAssociationErrors(association, associationPipelineErrors);
        } else {
            log.info("Updating association details");
            associationReportService.updateAssociationReportDetails(association);
        }

        // Save data
        if (!snpToLocationsMap.isEmpty()) {
            log.info("Updating location details ...");
            snpLocationMappingService.storeSnpLocation(snpToLocationsMap);
            log.info("Updating location details complete");
        }
        if (!allGenomicContexts.isEmpty()) {
            log.info("Updating genomic context details ...");
            snpGenomicContextMappingService.processGenomicContext(allGenomicContexts);
            log.info("Updating genomic context details complete");
        }
    }
}
