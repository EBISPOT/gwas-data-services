package uk.ac.ebi.spot.gwas.service.mapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.AppConfig;
import uk.ac.ebi.spot.gwas.constant.OperationMode;
import uk.ac.ebi.spot.gwas.dto.EnsemblData;
import uk.ac.ebi.spot.gwas.dto.EnsemblMappingResult;
import uk.ac.ebi.spot.gwas.dto.MappingDto;
import uk.ac.ebi.spot.gwas.dto.Variation;
import uk.ac.ebi.spot.gwas.model.Location;
import uk.ac.ebi.spot.gwas.service.loader.VariationService;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class DataMappingService {

    @Autowired
    private Mapper mapper;
    @Autowired
    private AppConfig config;
    @Autowired
    private VariationService variationService;

    public EnsemblMappingResult mappingPipeline(EnsemblData ensemblData,
                                                String snpRsId, Collection<String> reportedGenes,
                                                OperationMode mode) {

        log.info("Mapping pipeline commenced");
        Variation variation;
        if (mode == OperationMode.MAP_ALL_SNPS_INDB){
            mapper.setEnsemblData(ensemblData);
            variation = ensemblData.getVariations().get(snpRsId);
        }else {
            variation = variationService.getVariationFromDB(snpRsId);
        }

        EnsemblMappingResult mappingResult = new EnsemblMappingResult();
        mappingResult.setRsId(snpRsId);

        if (variation != null) {
            if (Optional.ofNullable(variation.getError()).isPresent()) {
                mappingResult.addPipelineErrors(variation.getError());
            } else {

                String currentRsId = variation.getName();
                if (!currentRsId.equals(snpRsId)) {
                    mappingResult.setMerged(1);
                    mappingResult.setCurrentSnpId(currentRsId);
                }

                if (Optional.ofNullable(variation.getFailed()).isPresent()) {
                    mappingResult.addPipelineErrors(variation.getFailed());
                }

                // Mapping and genomic context calls
                Collection<Location> locations = mapper.getMappings(variation, mode);
                mappingResult.setLocations(locations);

                // Add genomic context
                if (!locations.isEmpty()) {
                    // Functional class (most severe consequence). This implies there is at least one variant location.
                    if (Optional.ofNullable(variation.getMostSevereConsequence()).isPresent()) {
                        mappingResult.setFunctionalClass(variation.getMostSevereConsequence());
                    }

                    for (Location snpLocation : locations) {

                        // Overlapping genes
                        MappingDto ncbiOverlap = mapper.getOverlapGenes(snpLocation, config.getNcbiSource(), mappingResult, mode);
                        ncbiOverlap.getGeneNames().forEach(mappingResult::addNcbiOverlappingGene);
                        ncbiOverlap.getGenomicContexts().forEach(mappingResult::addGenomicContext);

                        MappingDto ensemblOverlap = mapper.getOverlapGenes(snpLocation, config.getEnsemblSource(), mappingResult, mode);
                        ensemblOverlap.getGeneNames().forEach(mappingResult::addEnsemblOverlappingGene);
                        ensemblOverlap.getGenomicContexts().forEach(mappingResult::addGenomicContext);

                        // Upstream Genes
                        mapper.getUpstreamGenes(snpLocation, config.getNcbiSource(), mappingResult, mode).forEach(mappingResult::addGenomicContext);
                        mapper.getUpstreamGenes(snpLocation, config.getEnsemblSource(), mappingResult, mode).forEach(mappingResult::addGenomicContext);

                        // Downstream Genes
                        mapper.getDownstreamGenes(snpLocation, config.getNcbiSource(), mappingResult, mode).forEach(mappingResult::addGenomicContext);
                        mapper.getDownstreamGenes(snpLocation, config.getEnsemblSource(), mappingResult, mode).forEach(mappingResult::addGenomicContext);
                    }
                }
            }
        } else {
            log.error("Variation call for SNP {} returned no result", snpRsId);
        }

        if (reportedGenes.isEmpty()) {
            String pipelineError = mapper.checkReportedGenes(reportedGenes, mappingResult.getLocations(), mode);
            mappingResult.addPipelineErrors(pipelineError);
        }

        return mappingResult;
    }

}
