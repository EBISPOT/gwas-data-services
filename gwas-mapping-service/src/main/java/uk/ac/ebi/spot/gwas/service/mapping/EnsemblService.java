package uk.ac.ebi.spot.gwas.service.mapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.AppConfig;
import uk.ac.ebi.spot.gwas.constant.DataType;
import uk.ac.ebi.spot.gwas.dto.*;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.Gene;
import uk.ac.ebi.spot.gwas.model.Locus;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.service.data.MappingRecordService;
import uk.ac.ebi.spot.gwas.service.data.SingleNucleotidePolymorphismQueryService;
import uk.ac.ebi.spot.gwas.service.data.TrackingOperationService;
import uk.ac.ebi.spot.gwas.util.MappingUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class EnsemblService {


    @Autowired
    private DataLoadingService dataLoadingService;
    @Autowired
    private AppConfig config;
    @Autowired
    private DataMappingService dataMappingService;
    @Autowired
    private  DataSavingService dataSavingService;
    @Autowired
    private SingleNucleotidePolymorphismQueryService singleNucleotidePolymorphismQueryService

    @Autowired
    private TrackingOperationService trackingOperationService;
    @Autowired
    private MappingRecordService mappingRecordService;

    public EnsemblData loadMappingData(List<String> snpRsIds,
                                       List<String> reportedGenes,
                                       int threadSize,
                                       int apiBatchSize) throws ExecutionException, InterruptedException, IOException {

        Path path = Paths.get(config.getCacheDir());
        Files.createDirectories(path);

        Map<String, GeneSymbol> reportedGeneMap = dataLoadingService.getReportedGenes(threadSize, apiBatchSize, reportedGenes);
        Map<String, Variation> variantMap = dataLoadingService.getVariation(threadSize, apiBatchSize, snpRsIds);
        variantMap = dataLoadingService.getVariationsWhoseRsidHasChanged(variantMap, snpRsIds);

        List<Variation> variants = new ArrayList<>();
        variantMap.forEach((k, v) -> {
            if (v.getMappings() != null) {
                variants.add(v);
            }
        });

        // Get CytoGenetic Bands
        List<String> locations = MappingUtil.getAllChromosomesAndPositions(variants);
        Map<String, List<OverlapRegion>> cytoGeneticBand = dataLoadingService.getCytoGeneticBands(DataType.CYTOGENETIC_BAND, locations);

        // Get Chromosome End
        List<String> chromosomes = MappingUtil.getAllChromosomes(variants);
        Map<String, AssemblyInfo> assemblyInfos = dataLoadingService.getAssemblyInfo(DataType.ASSEMBLY_INFO, chromosomes);

        // Get Overlapping genes
        Map<String, List<OverlapGene>> ensemblOverlappingGenes = dataLoadingService.getOverlappingGenes(DataType.ENSEMBL_OVERLAP_GENES, config.getEnsemblSource(), locations);
        Map<String, List<OverlapGene>> ncbiOverlappingGenes = dataLoadingService.getOverlappingGenes(DataType.NCBI_OVERLAP_GENES, config.getNcbiSource(), locations);

        // Get Upstream Genes
        List<String> upstreamLocations = MappingUtil.getUpstreamLocations(variants, config.getGenomicDistance());
        ensemblOverlappingGenes.putAll(dataLoadingService.getOverlappingGenes(DataType.ENSEMBL_UPSTREAM_GENES, config.getEnsemblSource(), upstreamLocations));
        ncbiOverlappingGenes.putAll(dataLoadingService.getOverlappingGenes(DataType.NCBI_UPSTREAM_GENES, config.getNcbiSource(), upstreamLocations));

        // Get Downstream Genes
        List<String> downStreamLocations = MappingUtil.getDownstreamLocations(variants, assemblyInfos, config.getGenomicDistance());
        ensemblOverlappingGenes.putAll(dataLoadingService.getOverlappingGenes(DataType.ENSEMBL_DOWNSTREAM_GENES, config.getEnsemblSource(), downStreamLocations));
        ncbiOverlappingGenes.putAll(dataLoadingService.getOverlappingGenes(DataType.NCBI_DOWNSTREAM_GENES, config.getNcbiSource(), downStreamLocations));


        return EnsemblData.builder()
                .variations(variantMap)
                .reportedGenes(reportedGeneMap)
                .cytoGeneticBand(cytoGeneticBand)
                .assemblyInfo(assemblyInfos)
                .ensemblOverlapGene(ensemblOverlappingGenes)
                .ncbiOverlapGene(ncbiOverlappingGenes)
                .build();
    }

    public MappingDto mapAndSaveData(Association association, EnsemblData ensemblData) {

        log.info("commenced saving Association {} Data", association.getId());
        MappingDto mappingDto = MappingDto.builder().build();
        Collection<Locus> studyAssociationLoci = association.getLoci();
        for (Locus associationLocus : studyAssociationLoci) {
            Long locusId = associationLocus.getId();
            log.info("Locus ID for this asscoiation {} is {}", association.getId(), locusId);
            Collection<SingleNucleotidePolymorphism> snpsLinkedToLocus = singleNucleotidePolymorphismQueryService.findByRiskAllelesLociId(locusId);
            Collection<Gene> authorReportedGenesLinkedToSnp = associationLocus.getAuthorReportedGenes();

            Collection<String> authorReportedGeneNamesLinkedToSnp = new ArrayList<>();
            authorReportedGenesLinkedToSnp.forEach(g -> {
                if (g.getGeneName() != null) {
                    authorReportedGeneNamesLinkedToSnp.add(g.getGeneName().trim());
                }
            });
            log.info("Reported Genes for this asscoiation {} ", authorReportedGeneNamesLinkedToSnp);
            for (SingleNucleotidePolymorphism snpLinkedToLocus : snpsLinkedToLocus) {
                String snpRsId = snpLinkedToLocus.getRsId();
                EnsemblMappingResult mappingResult = dataMappingService.mappingPipeline(ensemblData, snpRsId, authorReportedGeneNamesLinkedToSnp);
                mappingDto = dataSavingService.saveMappedData(snpLinkedToLocus, mappingResult);
            }
        }

        dataSavingService.createAssociationReports(association, mappingDto);

        trackingOperationService.update(association, user, "ASSOCIATION_MAPPING");
        log.debug("Update mapping record");
        mappingRecordService.updateAssociationMappingRecord(association, new Date(), performer);

        log.info(" Mapping was successful ");
        return mappingDto;
    }
}
