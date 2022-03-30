package uk.ac.ebi.spot.gwas.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.assembly_info.AssemblyInfo;
import uk.ac.ebi.spot.gwas.association.Association;
import uk.ac.ebi.spot.gwas.common.config.AppConfig;
import uk.ac.ebi.spot.gwas.common.constant.Uri;
import uk.ac.ebi.spot.gwas.mapping.dto.*;
import uk.ac.ebi.spot.gwas.common.model.*;
import uk.ac.ebi.spot.gwas.common.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.gwas.ensembl_data.EnsemblData;
import uk.ac.ebi.spot.gwas.gene_symbol.GeneSymbol;
import uk.ac.ebi.spot.gwas.overlap_gene.OverlapGene;
import uk.ac.ebi.spot.gwas.overlap_region.OverlapRegion;
import uk.ac.ebi.spot.gwas.common.service.*;
import uk.ac.ebi.spot.gwas.common.util.MappingUtil;
import uk.ac.ebi.spot.gwas.variation.Variation;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MappingSavingService {

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


    public void postMappingReportCheck(List<Association> associations){
        associationReportService.reportCheck(associations);
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

    public void saveRestHistory(EnsemblData ensembleData,
                                String eRelease,
                                int threadSize) throws ExecutionException, InterruptedException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        log.info(" Commence aggregating variation Rest History");
        List<EnsemblRestcallHistory> histories = new ArrayList<>();
        for (Map.Entry<String, Variation> entry : ensembleData.getVariations().entrySet()) {
            String snpRsId = entry.getKey();
            Variation variation = entry.getValue();
            if (variation.getMappings() != null) {
                String url = String.format("%s/%s/%s", config.getServer(), Uri.VARIATION, snpRsId);
                RestResponseResult result = MappingUtil.successResult(url, mapper.writeValueAsString(variation));
                histories.add(historyService.build(result, "snp", snpRsId, eRelease));
            }
        }
        this.saveHistory(histories, threadSize);

        histories = new ArrayList<>();
        log.info(" Commence aggregating reported Gene Rest History");
        for (Map.Entry<String, GeneSymbol> entry : ensembleData.getReportedGenes().entrySet()) {
            String gene = entry.getKey();
            GeneSymbol geneSymbol = entry.getValue();
            String url = String.format("%s/%s", config.getServer(), Uri.REPORTED_GENES);
            RestResponseResult result = MappingUtil.successResult(url, mapper.writeValueAsString(geneSymbol));
            histories.add(historyService.build(result, "lookup_symbol", gene, eRelease));
        }
        this.saveHistory(histories, threadSize);

        histories = new ArrayList<>();
        log.info(" Commence aggregating assemblyInfo Rest History");
        for (Map.Entry<String, AssemblyInfo> entry : ensembleData.getAssemblyInfo().entrySet()) {
            String chromosome = entry.getKey();
            AssemblyInfo assemblyInfo = entry.getValue();
            String url = String.format("%s/%s/%s", config.getServer(), Uri.INFO_ASSEMBLY, chromosome);
            RestResponseResult result = MappingUtil.successResult(url, mapper.writeValueAsString(assemblyInfo));
            histories.add(historyService.build(result, "info_assembly", chromosome, eRelease));
        }
        this.saveHistory(histories, threadSize);

        histories = new ArrayList<>();
        log.info(" Commence aggregating cytogenetic band Rest History");
        for (Map.Entry<String, List<OverlapRegion>> entry : ensembleData.getCytoGeneticBand().entrySet()) {
            String key = entry.getKey();
            List<OverlapRegion> overlapRegions = entry.getValue();
            String param = String.format("%s?feature=band", key);
            String uri = String.format("%s/%s/%s", config.getServer(), Uri.OVERLAP_BAND_REGION, param);
            RestResponseResult result = MappingUtil.successResult(uri, mapper.writeValueAsString(overlapRegions));
            histories.add(historyService.build(result, "overlap_region", param, eRelease));
        }
        this.saveHistory(histories, threadSize);

        histories = new ArrayList<>();
        log.info(" Commence aggregating ensembl overlapping gene Rest History");
        for (Map.Entry<String, List<OverlapGene>> entry : ensembleData.getEnsemblOverlapGene().entrySet()) {
            String key = entry.getKey();
            List<OverlapGene> value = entry.getValue();
            String param = String.format("%s?feature=gene", key);
            String uri = String.format("%s/%s/%s", config.getServer(), Uri.OVERLAPPING_GENE_REGION, param);
            RestResponseResult result = MappingUtil.successResult(uri, mapper.writeValueAsString(value));
            histories.add(historyService.build(result, "overlap_region", param, eRelease));
        }
        this.saveHistory(histories, threadSize);

        histories = new ArrayList<>();
        log.info(" Commence aggregating ncbi overlapping gene Rest History");
        for (Map.Entry<String, List<OverlapGene>> entry : ensembleData.getNcbiOverlapGene().entrySet()) {
            String location = entry.getKey();
            List<OverlapGene> overlapGenes = entry.getValue();
            String param = String.format("%s?feature=gene", location);
            String uri = String.format("%s/%s/%s", config.getServer(), Uri.OVERLAPPING_GENE_REGION, param);
            uri = String.format("%s&logic_name=%s&db_type=%s", uri, config.getNcbiLogicName(), config.getNcbiDbType());
            RestResponseResult result = MappingUtil.successResult(uri, mapper.writeValueAsString(overlapGenes));
            histories.add(historyService.build(result, "overlap_region", param, eRelease));
        }
        this.saveHistory(histories, threadSize);

    }

    void saveHistory(List<EnsemblRestcallHistory> histories, int threadSize) throws ExecutionException, InterruptedException {
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
