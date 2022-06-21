package uk.ac.ebi.spot.gwas.mapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.association.Association;
import uk.ac.ebi.spot.gwas.common.config.AppConfig;
import uk.ac.ebi.spot.gwas.common.constant.OperationMode;
import uk.ac.ebi.spot.gwas.mapping.dto.*;
import uk.ac.ebi.spot.gwas.common.model.*;
import uk.ac.ebi.spot.gwas.ensembl_data.EnsemblData;
import uk.ac.ebi.spot.gwas.common.service.MappingRecordService;
import uk.ac.ebi.spot.gwas.common.service.SecureUserRepository;
import uk.ac.ebi.spot.gwas.common.service.SingleNucleotidePolymorphismQueryService;
import uk.ac.ebi.spot.gwas.common.service.TrackingOperationService;
import uk.ac.ebi.spot.gwas.variation.Variant;
import uk.ac.ebi.spot.gwas.variation.VariationService;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class MappingService {

    @Autowired
    private MappingSavingService dataSavingService;
    @Autowired
    private SingleNucleotidePolymorphismQueryService singleNucleotidePolymorphismQueryService;
    @Autowired
    private TrackingOperationService trackingOperationService;
    @Autowired
    private SecureUserRepository secureUserRepository;
    @Autowired
    private MappingRecordService mappingRecordService;

    @Autowired
    private MappingFacade mappingFacade;
    @Autowired
    private AppConfig config;
    @Autowired
    private VariationService variationService;

    @Async("asyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<MappingDto> mapAndSaveData(Association association, EnsemblData ensemblData, OperationMode mode) {

        log.info("commenced mapping and saving Association {} Data", association.getId());
        MappingDto mappingDto = MappingDto.builder().build();
        Collection<Locus> studyAssociationLoci = association.getLoci();

        for (Locus associationLocus : studyAssociationLoci) {
            Long locusId = associationLocus.getId();
            Collection<SingleNucleotidePolymorphism> snpsLinkedToLocus = singleNucleotidePolymorphismQueryService.findByRiskAllelesLociId(locusId);
            Collection<Gene> authorReportedGenesLinkedToSnp = associationLocus.getAuthorReportedGenes();

            Collection<String> authorReportedGeneNamesLinkedToSnp = new ArrayList<>();
            authorReportedGenesLinkedToSnp.forEach(g -> {
                if (g.getGeneName() != null) {
                    authorReportedGeneNamesLinkedToSnp.add(g.getGeneName().trim());
                }
            });
            for (SingleNucleotidePolymorphism snpLinkedToLocus : snpsLinkedToLocus) {
                String snpRsId = snpLinkedToLocus.getRsId();
                EnsemblMappingResult mappingResult = this.mappingPipeline(ensemblData, snpRsId, authorReportedGeneNamesLinkedToSnp, mode);
                mappingDto = dataSavingService.saveMappedData(snpLinkedToLocus, mappingResult);
            }
        }

        dataSavingService.createAssociationReports(association, mappingDto);

        SecureUser user = secureUserRepository.findByEmail("automatic_mapping_process");
        String performer = "automatic_mapping_process";

        trackingOperationService.update(association, user, "ASSOCIATION_MAPPING");
        log.debug("Update mapping record");
        mappingRecordService.updateAssociationMappingRecord(association, new Date(), performer);

        log.info(" Mapping was successful ");
        return CompletableFuture.completedFuture(mappingDto);
    }


    public EnsemblMappingResult mappingPipeline(EnsemblData ensemblData, String snpRsId,
                                                Collection<String> reportedGenes, OperationMode mode) {

        log.info("Mapping pipeline commenced");
        Variant variant;
        if (mode == OperationMode.MAP_ALL_SNPS_INDB){
            mappingFacade.setEnsemblData(ensemblData);
            variant = ensemblData.getVariations().get(snpRsId);
        }else {
            variant = variationService.getVariationFromDB(snpRsId);
        }

        EnsemblMappingResult mappingResult = new EnsemblMappingResult();
        mappingResult.setRsId(snpRsId);

        if (variant != null) {
            if (Optional.ofNullable(variant.getError()).isPresent()) {
                mappingResult.addPipelineErrors(variant.getError());
            } else {

                String currentRsId = variant.getName();
                if (!currentRsId.equals(snpRsId)) {
                    mappingResult.setMerged(1);
                    mappingResult.setCurrentSnpId(currentRsId);
                }

                if (Optional.ofNullable(variant.getFailed()).isPresent()) {
                    mappingResult.addPipelineErrors(variant.getFailed());
                }

                // Mapping and genomic context calls
                Collection<Location> locations = mappingFacade.getMappings(variant, mode);
                mappingResult.setLocations(locations);

                // Add genomic context
                if (!locations.isEmpty()) {
                    // Functional class (most severe consequence). This implies there is at least one variant location.
                    if (Optional.ofNullable(variant.getMostSevereConsequence()).isPresent()) {
                        mappingResult.setFunctionalClass(variant.getMostSevereConsequence());
                    }

                    for (Location snpLocation : locations) {

                        // Overlapping genes
                        MappingDto ncbiOverlap = mappingFacade.getOverlapGenes(snpLocation, config.getNcbiSource(), mappingResult, mode);
                        ncbiOverlap.getGeneNames().forEach(mappingResult::addNcbiOverlappingGene);
                        ncbiOverlap.getGenomicContexts().forEach(mappingResult::addGenomicContext);

                        MappingDto ensemblOverlap = mappingFacade.getOverlapGenes(snpLocation, config.getEnsemblSource(), mappingResult, mode);
                        ensemblOverlap.getGeneNames().forEach(mappingResult::addEnsemblOverlappingGene);
                        ensemblOverlap.getGenomicContexts().forEach(mappingResult::addGenomicContext);

                        // Upstream Genes
                        mappingFacade.getUpstreamGenes(snpLocation, config.getNcbiSource(), mappingResult, mode).forEach(mappingResult::addGenomicContext);
                        mappingFacade.getUpstreamGenes(snpLocation, config.getEnsemblSource(), mappingResult, mode).forEach(mappingResult::addGenomicContext);

                        // Downstream Genes
                        mappingFacade.getDownstreamGenes(snpLocation, config.getNcbiSource(), mappingResult, mode).forEach(mappingResult::addGenomicContext);
                        mappingFacade.getDownstreamGenes(snpLocation, config.getEnsemblSource(), mappingResult, mode).forEach(mappingResult::addGenomicContext);
                    }
                }
            }
        } else {
            log.error("Variation call for SNP {} returned no result", snpRsId);
        }

        if (reportedGenes.isEmpty()) {
            String pipelineError = mappingFacade.checkReportedGenes(reportedGenes, mappingResult.getLocations(), mode);
            mappingResult.addPipelineErrors(pipelineError);
        }

        return mappingResult;
    }
}
