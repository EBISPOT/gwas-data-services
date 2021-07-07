package uk.ac.ebi.spot.gwas.service.mapping;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.AppConfig;
import uk.ac.ebi.spot.gwas.constant.Uri;
import uk.ac.ebi.spot.gwas.dto.*;
import uk.ac.ebi.spot.gwas.model.*;
import uk.ac.ebi.spot.gwas.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.gwas.service.data.*;
import uk.ac.ebi.spot.gwas.util.MappingUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    @Autowired
    private EnsemblRestcallHistoryService historyService;
    @Autowired
    private AppConfig config;

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


    public void createErrors(Association association){
        associationReportService.processAssociationErrors(association, new ArrayList<>());
    }

    void createAssociationReports(Association association, MappingDto mappingDto) {

        Collection<GenomicContext> allGenomicContexts = mappingDto.getAllGenomicContexts();
        Map<String, Set<Location>> snpToLocationsMap = mappingDto.getSnpToLocationsMap();
        Collection<String> associationPipelineErrors = mappingDto.getAssociationPipelineErrors();

        // Create association report based on whether there is errors or not
        if (!associationPipelineErrors.isEmpty()) {
            log.info("Processing association errors");
            associationReportService.processAssociationErrors(association, associationPipelineErrors);
        } else {
            log.info("Updating association details with Reports");
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


    public void saveRestHistory(EnsemblData ensembleData, String eRelease, int threadSize) throws ExecutionException, InterruptedException {

        log.info(" Commence aggregating variation Rest History");
        List<EnsemblRestcallHistory> histories = new ArrayList<>();
        ensembleData.getVariations().forEach((snpRsId, variation) -> {
            if (variation.getMappings() != null) {
                String url = String.format("%s/%s/%s", config.getServer(), Uri.VARIATION, snpRsId);
                RestResponseResult result = MappingUtil.successResult(url, variation.toString());
                histories.add(historyService.build(result, "snp", snpRsId, eRelease));
            }
        });

        log.info(" Commence aggregating reported Gene Rest History");
        ensembleData.getReportedGenes().forEach((gene, geneSymbol) -> {
            String url = String.format("%s/%s", config.getServer(), Uri.REPORTED_GENES);
            RestResponseResult result = MappingUtil.successResult(url, geneSymbol.toString());
            histories.add(historyService.build(result, "lookup_symbol", gene, eRelease));
        });

        log.info(" Commence aggregating assemblyInfo Rest History");
        ensembleData.getAssemblyInfo().forEach((chromosome, assemblyInfo) -> {
            String url = String.format("%s/%s/%s", config.getServer(), Uri.INFO_ASSEMBLY, chromosome);
            RestResponseResult result = MappingUtil.successResult(url, assemblyInfo.toString());
            histories.add(historyService.build(result, "info_assembly", chromosome, eRelease));
        });

        log.info(" Commence aggregating cytogenetic band Rest History");
        ensembleData.getCytoGeneticBand().forEach((location, overlapRegions) -> {
            String param = String.format("%s?feature=band", location);
            String uri = String.format("%s/%s/%s", config.getServer(), Uri.OVERLAP_BAND_REGION, param);
            RestResponseResult result = MappingUtil.successResult(uri, String.valueOf(overlapRegions));
            histories.add(historyService.build(result, "overlap_region", param, eRelease));
        });

        log.info(" Commence aggregating ensembl overlapping gene Rest History");
        ensembleData.getEnsemblOverlapGene().forEach((location, overlapGenes) -> {
            String param = String.format("%s?feature=gene", location);
            String uri = String.format("%s/%s/%s", config.getServer(), Uri.OVERLAPPING_GENE_REGION, param);
            RestResponseResult result = MappingUtil.successResult(uri, String.valueOf(overlapGenes));
            histories.add(historyService.build(result, "overlap_region", param, eRelease));
        });

        log.info(" Commence aggregating ncbi overlapping gene Rest History");
        ensembleData.getNcbiOverlapGene().forEach((location, overlapGenes) -> {
            String param = String.format("%s?feature=gene", location);
            String uri = String.format("%s/%s/%s", config.getServer(), Uri.OVERLAPPING_GENE_REGION, param);
            uri = String.format("%s&logic_name=%s&db_type=%s", uri, config.getNcbiLogicName(), config.getNcbiDbType());
            RestResponseResult result = MappingUtil.successResult(uri, String.valueOf(overlapGenes));
            histories.add(historyService.build(result, "overlap_region", param, eRelease));
        });
        log.info("Saving {} ensembl call histories in batch ", histories.size());

        List<EnsemblRestcallHistory> restcallHistories = new ArrayList<>();

        int batchSize = 10;
        int partitionSize = threadSize * batchSize;
        for (List<EnsemblRestcallHistory> dataPartition : ListUtils.partition(histories, partitionSize)) {
            List<CompletableFuture<List<EnsemblRestcallHistory>>> futureList = ListUtils.partition(dataPartition, batchSize)
                    .stream()
                    .map(listPart -> historyService.create(listPart)).collect(Collectors.toList());

            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
            for (CompletableFuture<List<EnsemblRestcallHistory>> future : futureList) {
                restcallHistories.addAll(future.get());
            }
            log.info("Finished Processing {} History", restcallHistories.size());
        }
    }

}
